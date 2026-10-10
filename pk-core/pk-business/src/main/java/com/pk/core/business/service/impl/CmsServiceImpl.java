package com.pk.core.business.service.impl;

import lombok.RequiredArgsConstructor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pk.core.business.repository.CmsBlockRepo;
import com.pk.core.business.repository.CmsPageRepo;
import com.pk.core.business.repository.CollectionRepo;
import com.pk.core.business.repository.ProductRepo;
import com.pk.core.business.service.BannerService;
import com.pk.core.business.service.CmsService;
import com.pk.core.business.service.ProductService;
import com.pk.core.common.exception.BusinessException;
import com.pk.core.common.exception.ErrorCode;
import com.pk.core.common.exception.ResourceNotFoundException;
import com.pk.core.common.util.SlugUtil;
import com.pk.core.model.dto.request.AdminCmsBlockRequestDto;
import com.pk.core.model.dto.request.AdminCmsPageRequestDto;
import com.pk.core.model.dto.response.BannerPlacementRenderResponseDto;
import com.pk.core.model.dto.response.CmsBlockResponseDto;
import com.pk.core.model.dto.response.CmsPageResponseDto;
import com.pk.core.model.dto.response.CmsStorefrontBlockResponseDto;
import com.pk.core.model.dto.response.CmsStorefrontPageResponseDto;
import com.pk.core.model.dto.response.ProductResponseDto;
import com.pk.core.model.entity.CmsBlockEntity;
import com.pk.core.model.entity.CollectionEntity;
import com.pk.core.model.entity.CmsPageEntity;
import com.pk.core.model.entity.ProductEntity;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CmsServiceImpl implements CmsService {

    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {
    };
    private static final Set<String> BANNER_LAYOUTS = Set.of("SLIDER", "GRID", "DOUBLE");
    private static final Set<String> PAGE_STATUSES = Set.of(CmsPageEntity.DRAFT, CmsPageEntity.PUBLISHED);
    private static final int DEFAULT_CAROUSEL_LIMIT = 12;
    private static final int MAX_CAROUSEL_LIMIT = 50;

    private final CmsPageRepo pages;
    private final CmsBlockRepo blocks;
    private final ObjectMapper objectMapper;
    private final BannerService bannerService;
    private final ProductService productService;
    private final ProductRepo products;
    private final CollectionRepo collections;

    // ============================== Admin ==============================

    @Transactional(readOnly = true)
    @Override
    public List<CmsPageResponseDto> findAllPages() {
        // findAll() KHÔNG tự lọc soft-delete (Mirage/PkRepo không biết deleted_date) - lọc tay bằng Java.
        return pages.findAll(Sort.unsorted()).stream()
                .filter(p -> p.getDeletedDate() == null)
                .map(p -> CmsPageResponseDto.from(p, toBlockDtos(p.getId())))
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public CmsPageResponseDto findPageById(Long pageId) {
        CmsPageEntity page = findPageOrThrow(pageId);
        return CmsPageResponseDto.from(page, toBlockDtos(page.getId()));
    }

    @Transactional
    @Override
    public CmsPageResponseDto createPage(AdminCmsPageRequestDto req) {
        String slug = resolveSlug(req.getSlug(), req.getName());
        if (pages.findBySlug(slug) != null) {
            throw BusinessException.conflict(ErrorCode.DUPLICATE_SLUG, "Đường dẫn (slug) đã tồn tại: " + slug, slug);
        }
        // Kiểm tra block TRƯỚC khi ghi để lỗi cấu trúc không để lại trang dở dang.
        List<CmsBlockEntity> newBlocks = buildBlocks(null, req.getBlocks());
        CmsPageEntity page = new CmsPageEntity(slug, req.getName().trim(), normalizeStatus(req.getStatus()));
        pages.create(page);
        for (CmsBlockEntity b : newBlocks) {
            b.setPageId(page.getId());
            blocks.create(b);
        }
        return CmsPageResponseDto.from(page, toBlockDtos(page.getId()));
    }

    @Transactional
    @Override
    public CmsPageResponseDto updatePage(Long pageId, AdminCmsPageRequestDto req) {
        CmsPageEntity page = findPageOrThrow(pageId);

        String slug = resolveSlug(req.getSlug(), req.getName());
        if (!slug.equals(page.getSlug()) && pages.findBySlug(slug) != null) {
            throw BusinessException.conflict(ErrorCode.DUPLICATE_SLUG, "Đường dẫn (slug) đã tồn tại: " + slug, slug);
        }
        List<CmsBlockEntity> newBlocks = req.getBlocks() == null ? null : buildBlocks(pageId, req.getBlocks());

        page.setSlug(slug);
        page.setTitle(req.getName().trim());
        if (req.getStatus() != null && !req.getStatus().isBlank()) {
            page.setStatus(normalizeStatus(req.getStatus()));
        }
        page.touch();
        pages.update(page);

        // blocks == null -> giữ nguyên blocks hiện có. blocks != null (kể cả []) -> THAY TOÀN BỘ (xoá hết rồi tạo lại).
        if (newBlocks != null) {
            for (CmsBlockEntity old : blocks.findByPageId(pageId)) {
                blocks.delete(old);
            }
            for (CmsBlockEntity b : newBlocks) {
                blocks.create(b);
            }
        }
        return CmsPageResponseDto.from(page, toBlockDtos(pageId));
    }

    @Transactional
    @Override
    public CmsBlockResponseDto addBlock(Long pageId, AdminCmsBlockRequestDto req) {
        findPageOrThrow(pageId);
        CmsBlockEntity block = buildBlock(pageId, req);
        blocks.create(block);
        return toBlockDto(block);
    }

    @Transactional
    @Override
    public CmsBlockResponseDto updateBlock(Long pageId, Long blockId, AdminCmsBlockRequestDto req) {
        findPageOrThrow(pageId);
        CmsBlockEntity block = findBlockOrThrow(pageId, blockId);
        CmsBlockEntity fresh = buildBlock(pageId, req);
        block.setType(fresh.getType());
        block.setSortOrder(fresh.getSortOrder());
        block.setData(fresh.getData());
        block.setTargetSegment(fresh.getTargetSegment());
        block.touch();
        blocks.update(block);
        return toBlockDto(block);
    }

    @Transactional
    @Override
    public void deleteBlock(Long pageId, Long blockId) {
        findPageOrThrow(pageId);
        CmsBlockEntity block = findBlockOrThrow(pageId, blockId);
        blocks.delete(block);
    }

    // ============================== Storefront ==============================

    @Transactional(readOnly = true)
    @Override
    public CmsStorefrontPageResponseDto findPublishedBySlug(String slug) {
        CmsPageEntity page = (slug == null || slug.isBlank()) ? null : pages.findBySlug(slug.trim().toLowerCase());
        if (page == null || page.getDeletedDate() != null || !CmsPageEntity.PUBLISHED.equals(page.getStatus())) {
            throw new ResourceNotFoundException("Trang CMS", "CmsPage", slug);
        }
        List<CmsStorefrontBlockResponseDto> out = new ArrayList<>();
        for (CmsBlockEntity b : blocks.findByPageId(page.getId())) {
            Map<String, Object> config = readMap(b.getData());
            out.add(new CmsStorefrontBlockResponseDto(String.valueOf(b.getId()), b.getType(), b.getSortOrder(), config,
                    resolveContent(b.getType(), config)));
        }
        return new CmsStorefrontPageResponseDto(String.valueOf(page.getId()), page.getTitle(), page.getSlug(),
                page.getStatus(), out);
    }

    /** Nạp sẵn dữ liệu cho các loại block có nội dung động; loại khác trả null (client tự dùng `config`). */
    private Map<String, Object> resolveContent(String type, Map<String, Object> config) {
        if (config == null) {
            return null;
        }
        if (CmsBlockEntity.BANNER.equals(type)) {
            return resolveBanner(config);
        }
        if (CmsBlockEntity.PRODUCT_CAROUSEL.equals(type)) {
            return resolveCarousel(config);
        }
        return null;
    }

    private Map<String, Object> resolveBanner(Map<String, Object> config) {
        Map<String, Object> content = new LinkedHashMap<>();
        String code = text(config.get("placementCode"));
        BannerPlacementRenderResponseDto render = null;
        if (code != null) {
            try {
                render = bannerService.renderByPlacementCode(code);
            } catch (ResourceNotFoundException ex) {
                render = null; // vị trí đã bị xoá/không tồn tại: trang vẫn hiển thị, block không có banner
            }
        }
        String layout = text(config.get("layout"));
        layout = layout == null ? null : layout.toUpperCase(Locale.ROOT);
        if (layout == null && render != null) {
            layout = layoutFromDisplayType(render.getDisplayType());
        }
        content.put("layout", layout);
        if (render != null) {
            Map<String, Object> placement = new LinkedHashMap<>();
            placement.put("code", render.getCode());
            placement.put("name", render.getName());
            placement.put("displayType", render.getDisplayType());
            content.put("placement", placement);
            content.put("banners", render.getBanners());
        } else {
            content.put("placement", null);
            content.put("banners", List.of());
        }
        return content;
    }

    private static String layoutFromDisplayType(String displayType) {
        if (displayType == null) {
            return null;
        }
        return switch (displayType.toUpperCase(Locale.ROOT)) {
            case "CAROUSEL" -> "SLIDER";
            case "GRID" -> "GRID";
            default -> displayType.toUpperCase(Locale.ROOT);
        };
    }

    /** Admin FE lưu nguồn dữ liệu carousel ở config.dataSource.{filterType,...}; khóa legacy cấp trên cùng vẫn được đọc. */
    private static Object carouselValue(Map<String, Object> config, String key) {
        Object v = config.get(key);
        if (v == null && config.get("dataSource") instanceof Map<?, ?> ds) {
            v = ds.get(key);
        }
        return v;
    }

    private static String carouselFilterType(Map<String, Object> config) {
        String t = text(carouselValue(config, "filterType"));
        return t == null ? null : t.toUpperCase(Locale.ROOT);
    }

    /** categorySlugs[] (+ categoryId, FE dùng làm slug đầu tiên), không trùng, giữ thứ tự. */
    private static List<String> carouselCategorySlugs(Map<String, Object> config) {
        java.util.LinkedHashSet<String> slugs = new java.util.LinkedHashSet<>();
        if (carouselValue(config, "categorySlugs") instanceof List<?> items) {
            for (Object item : items) {
                String t = text(item);
                if (t != null) {
                    slugs.add(t);
                }
            }
        }
        String legacy = text(carouselValue(config, "categoryId"));
        if (legacy != null) {
            slugs.add(legacy);
        }
        return new ArrayList<>(slugs);
    }

    /** collectionSlug, hoặc collectionId (id số -> tra slug; không phải số thì coi là slug). */
    private String carouselCollectionSlug(Map<String, Object> config) {
        String slug = text(carouselValue(config, "collectionSlug"));
        if (slug != null) {
            return slug;
        }
        String id = text(carouselValue(config, "collectionId"));
        if (id == null) {
            return null;
        }
        try {
            CollectionEntity c = collections.findOne(Long.valueOf(id));
            return c == null ? null : c.getSlug();
        } catch (NumberFormatException ex) {
            return id;
        }
    }

    private Map<String, Object> resolveCarousel(Map<String, Object> config) {
        List<ProductResponseDto> list = new ArrayList<>();
        int limit = carouselLimit(config);
        String filterType = carouselFilterType(config);
        Object ids = carouselValue(config, "productIds");
        if (filterType == null || "MANUAL_PRODUCTS".equals(filterType)) {
            // Legacy (không có filterType): productIds hoặc collectionSlug.
            if (ids instanceof List<?> items) {
                for (Object item : items) {
                    ProductResponseDto p = findProduct(item);
                    if (p != null) {
                        list.add(p);
                    }
                }
            } else if (filterType == null && carouselCollectionSlug(config) != null) {
                list.addAll(productService.search(null, carouselCollectionSlug(config), null, null, null, null,
                        "NEWEST", 1, limit).content());
            }
        } else if ("COLLECTION".equals(filterType)) {
            String slug = carouselCollectionSlug(config);
            if (slug != null) {
                list.addAll(productService.search(null, slug, null, null, null, null, "NEWEST", 1, limit).content());
            }
        } else if ("CATEGORY".equals(filterType)) {
            java.util.Set<Long> seen = new java.util.HashSet<>();
            for (String slug : carouselCategorySlugs(config)) {
                for (ProductResponseDto p : productService.search(slug, null, null, null, null, null, "NEWEST", 1, limit).content()) {
                    if (seen.add(p.getId()) && list.size() < limit) {
                        list.add(p);
                    }
                }
            }
        } else if ("NEW_ARRIVALS".equals(filterType)) {
            list.addAll(productService.search(null, null, null, null, null, null, "NEWEST", 1, limit).content());
        }
        // BEST_SELLERS / MOST_FAVORITED / CAMPAIGN: backend chưa có số liệu bán/yêu thích/chiến dịch -> danh sách rỗng.
        Map<String, Object> content = new LinkedHashMap<>();
        content.put("header", config.get("header"));
        content.put("products", list);
        return content;
    }

    /** `item` là id số hoặc slug sản phẩm; chỉ trả sản phẩm PUBLISHED (không có -> bỏ qua, không lỗi cả trang). */
    private ProductResponseDto findProduct(Object item) {
        String raw = text(item);
        if (raw == null) {
            return null;
        }
        String slug = raw;
        try {
            ProductEntity entity = products.findOne(Long.valueOf(raw));
            slug = entity == null ? null : entity.getSlug();
        } catch (NumberFormatException ex) {
            slug = raw;
        }
        if (slug == null) {
            return null;
        }
        try {
            return productService.findBySlug(slug);
        } catch (ResourceNotFoundException ex) {
            return null;
        }
    }

    /** FE gửi display.limit; khóa legacy config.limit vẫn được đọc. */
    private static int carouselLimit(Map<String, Object> config) {
        Object limit = config.get("limit");
        if (!(limit instanceof Number) && config.get("display") instanceof Map<?, ?> d) {
            limit = d.get("limit");
        }
        int value = limit instanceof Number n ? n.intValue() : DEFAULT_CAROUSEL_LIMIT;
        return Math.max(1, Math.min(MAX_CAROUSEL_LIMIT, value));
    }

    private static void validateCarouselConfig(Map<String, Object> config) {
        Object ids = carouselValue(config, "productIds");
        if (ids != null && !(ids instanceof List<?>)) {
            throw BusinessException.badRequest(ErrorCode.VALIDATION_FAILED, "config.dataSource.productIds phải là mảng");
        }
        boolean hasIds = ids instanceof List<?> l && !l.isEmpty();
        String filterType = carouselFilterType(config);
        if (filterType == null) {
            if (!hasIds && text(carouselValue(config, "collectionSlug")) == null) {
                throw BusinessException.badRequest(ErrorCode.VALIDATION_FAILED,
                        "Block PRODUCT_CAROUSEL cần config.dataSource.filterType, hoặc config.productIds / config.collectionSlug");
            }
        } else if ("MANUAL_PRODUCTS".equals(filterType) && !hasIds) {
            throw BusinessException.badRequest(ErrorCode.VALIDATION_FAILED,
                    "Block PRODUCT_CAROUSEL (MANUAL_PRODUCTS) cần dataSource.productIds có ít nhất 1 sản phẩm");
        } else if ("CATEGORY".equals(filterType) && carouselCategorySlugs(config).isEmpty()) {
            throw BusinessException.badRequest(ErrorCode.VALIDATION_FAILED,
                    "Block PRODUCT_CAROUSEL (CATEGORY) cần dataSource.categorySlugs hoặc dataSource.categoryId");
        } else if ("COLLECTION".equals(filterType)
                && text(carouselValue(config, "collectionSlug")) == null
                && text(carouselValue(config, "collectionId")) == null) {
            throw BusinessException.badRequest(ErrorCode.VALIDATION_FAILED,
                    "Block PRODUCT_CAROUSEL (COLLECTION) cần dataSource.collectionSlug hoặc dataSource.collectionId");
        }
    }

    // ============================== helpers ==============================

    private List<CmsBlockEntity> buildBlocks(Long pageId, List<AdminCmsBlockRequestDto> reqBlocks) {
        List<CmsBlockEntity> list = new ArrayList<>();
        if (reqBlocks != null) {
            for (AdminCmsBlockRequestDto b : reqBlocks) {
                list.add(buildBlock(pageId, b));
            }
        }
        return list;
    }

    /** Chuẩn hoá + kiểm tra block (type thuộc spec, config đúng cấu trúc tối thiểu theo loại). */
    private CmsBlockEntity buildBlock(Long pageId, AdminCmsBlockRequestDto req) {
        String type = req.getType() == null ? "" : req.getType().trim().toUpperCase(Locale.ROOT);
        if (!CmsBlockEntity.TYPES.contains(type)) {
            throw BusinessException.badRequest(ErrorCode.VALIDATION_FAILED,
                    "Loại block không hợp lệ: " + req.getType() + " (cho phép: " + new java.util.TreeSet<>(CmsBlockEntity.TYPES) + ")",
                    req.getType());
        }
        validateConfig(type, req.getConfig());
        String segment = req.getTargetSegment() == null || req.getTargetSegment().isBlank()
                ? null : req.getTargetSegment().trim();
        return new CmsBlockEntity(pageId, type, req.getSortOrder(), toJson(req.getConfig()), segment);
    }

    private static void validateConfig(String type, Map<String, Object> config) {
        if (config == null) {
            throw BusinessException.badRequest(ErrorCode.VALIDATION_FAILED, "config không được để trống");
        }
        if (CmsBlockEntity.BANNER.equals(type)) {
            if (text(config.get("placementCode")) == null) {
                throw BusinessException.badRequest(ErrorCode.VALIDATION_FAILED,
                        "Block BANNER cần config.placementCode (mã vị trí banner)");
            }
            String layout = text(config.get("layout"));
            if (layout != null && !BANNER_LAYOUTS.contains(layout.toUpperCase(Locale.ROOT))) {
                throw BusinessException.badRequest(ErrorCode.VALIDATION_FAILED,
                        "config.layout không hợp lệ: " + layout + " (SLIDER | GRID | DOUBLE)", layout);
            }
        } else if (CmsBlockEntity.PRODUCT_CAROUSEL.equals(type)) {
            validateCarouselConfig(config);
        }
        // INFO_CARDS, IMAGE_GALLERY và các loại còn lại: chỉ cần config là object (cấu trúc do client quy ước).
    }

    private static String normalizeStatus(String status) {
        if (status == null || status.isBlank()) {
            return CmsPageEntity.DRAFT;
        }
        String s = status.trim().toUpperCase(Locale.ROOT);
        if (!PAGE_STATUSES.contains(s)) {
            throw BusinessException.badRequest(ErrorCode.VALIDATION_FAILED,
                    "status không hợp lệ: " + status + " (DRAFT | PUBLISHED)", status);
        }
        return s;
    }

    private static String text(Object value) {
        if (value == null) {
            return null;
        }
        String s = String.valueOf(value).trim();
        return s.isEmpty() ? null : s;
    }

    private List<CmsBlockResponseDto> toBlockDtos(Long pageId) {
        return blocks.findByPageId(pageId).stream().map(this::toBlockDto).toList();
    }

    private CmsBlockResponseDto toBlockDto(CmsBlockEntity e) {
        CmsBlockResponseDto dto = new CmsBlockResponseDto(String.valueOf(e.getId()), e.getType(), e.getSortOrder(),
                readMap(e.getData()), e.getTargetSegment());
        dto.copyAudit(e);
        return dto;
    }

    private String toJson(Map<String, Object> config) {
        try {
            return objectMapper.writeValueAsString(config);
        } catch (JsonProcessingException ex) {
            throw BusinessException.badRequest(ErrorCode.VALIDATION_FAILED, "config không phải JSON hợp lệ");
        }
    }

    private Map<String, Object> readMap(String json) {
        if (json == null || json.isBlank()) {
            return new LinkedHashMap<>();
        }
        try {
            return objectMapper.readValue(json, MAP_TYPE);
        } catch (JsonProcessingException ex) {
            // Dữ liệu cũ (chuỗi thô không phải JSON object): bọc lại để không làm hỏng cả trang.
            Map<String, Object> raw = new LinkedHashMap<>();
            raw.put("raw", json);
            return raw;
        }
    }

    private CmsPageEntity findPageOrThrow(Long pageId) {
        CmsPageEntity page = pages.findOne(pageId);
        if (page == null || page.getDeletedDate() != null) {
            throw new ResourceNotFoundException("Trang CMS", "CmsPage", pageId);
        }
        return page;
    }

    /** IDOR-kiểu: đảm bảo blockId thực sự thuộc pageId trên URL. */
    private CmsBlockEntity findBlockOrThrow(Long pageId, Long blockId) {
        CmsBlockEntity block = blocks.findOne(blockId);
        if (block == null || !block.getPageId().equals(pageId)) {
            throw new ResourceNotFoundException("Block CMS", "CmsBlock", blockId);
        }
        return block;
    }

    private static String resolveSlug(String slug, String name) {
        return (slug == null || slug.isBlank()) ? SlugUtil.slugify(name) : SlugUtil.slugify(slug);
    }
}
