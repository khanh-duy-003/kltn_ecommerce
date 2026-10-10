package com.pk.core.business.service.impl;

import lombok.RequiredArgsConstructor;

import com.pk.core.business.repository.CategoryRepo;
import com.pk.core.business.repository.CollectionRepo;
import com.pk.core.business.repository.ProductRepo;
import com.pk.core.business.repository.ProductSkuRepo;
import com.pk.core.business.service.ProductService;
import com.pk.core.business.service.PromotionPricingService;
import com.pk.core.common.exception.BusinessException;
import com.pk.core.common.exception.ErrorCode;
import com.pk.core.common.exception.ResourceNotFoundException;
import com.pk.core.common.util.SlugUtil;
import com.pk.core.common.web.PageResponse;
import com.pk.core.model.dto.request.AdminCreateProductRequestDto;
import com.pk.core.model.dto.request.AdminSkuRequestDto;
import com.pk.core.model.dto.request.AdminUpdateProductRequestDto;
import com.pk.core.model.dto.response.CategoryResponseDto;
import com.pk.core.model.dto.response.CollectionResponseDto;
import com.pk.core.model.dto.response.ProductResponseDto;
import com.pk.core.model.dto.response.ProductSkuResponseDto;
import com.pk.core.model.entity.CategoryEntity;
import com.pk.core.model.entity.ProductEntity;
import com.pk.core.model.entity.ProductSkuEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/** Không lọc = truyền chuỗi rỗng/khoảng giá [0, MAX] xuống ProductRepo (xem lý do ở
 * ProductRepo_searchPublished.sql - tránh cú pháp SQL điều kiện động chưa có tiền lệ trong dự án).
 * Sort và phân trang làm ở tầng Java (không ORDER BY/LIMIT động trong SQL, xem ProductRepo). */
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private static final BigDecimal NO_MAX_PRICE = new BigDecimal("999999999999");

    private final ProductRepo products;
    private final ProductSkuRepo productSkus;
    private final CategoryRepo categories;
    private final CollectionRepo collections;
    private final PromotionPricingService promotionPricing;

    @Transactional(readOnly = true)
    @Override
    public ProductResponseDto findBySlug(String slug) {
        ProductEntity p = products.findBySlug(slug);
        if (p == null || !p.isPublished()) {
            throw new ResourceNotFoundException("Sản phẩm", "Product", slug);
        }
        return toDto(p);
    }

    @Transactional(readOnly = true)
    @Override
    public PageResponse<ProductResponseDto> search(String categorySlug, String collectionSlug, String keyword,
                                                     String material, BigDecimal minPrice, BigDecimal maxPrice,
                                                     String sort, int page, int take) {
        List<ProductEntity> matched = products.searchPublished(
                nullToEmpty(categorySlug), nullToEmpty(collectionSlug), nullToEmpty(keyword), nullToEmpty(material),
                minPrice != null ? minPrice : BigDecimal.ZERO,
                maxPrice != null ? maxPrice : NO_MAX_PRICE);

        List<ProductResponseDto> dtos = matched.stream().map(this::toDto).toList();
        dtos = sortDtos(dtos, sort);

        // page 1-based, take mặc định 20 tối đa 100 (quy ước chung, xem PageResponse.paginate).
        return PageResponse.paginate(dtos, page, take);
    }

    private List<ProductResponseDto> sortDtos(List<ProductResponseDto> dtos, String sort) {
        String s = sort == null ? "" : sort.trim().toUpperCase();
        Comparator<ProductResponseDto> cmp = switch (s) {
            case "PRICE_ASC" -> Comparator.comparing(ProductResponseDto::getPriceFrom,
                    Comparator.nullsLast(Comparator.naturalOrder()));
            case "PRICE_DESC" -> Comparator.comparing(ProductResponseDto::getPriceFrom,
                    Comparator.nullsLast(Comparator.naturalOrder())).reversed();
            // "NEWEST" và "RATING_DESC" (chưa có cột rating, tạm coi như NEWEST) đều rơi vào đây.
            default -> Comparator.comparing(ProductResponseDto::getPublishedAt,
                    Comparator.nullsLast(Comparator.naturalOrder())).reversed();
        };
        return dtos.stream().sorted(cmp.thenComparing(ProductResponseDto::getId, Comparator.reverseOrder())).toList();
    }

    private ProductResponseDto toDto(ProductEntity p) {
        CategoryEntity category = p.getCategoryId() != null ? categories.findOne(p.getCategoryId()) : null;

        List<ProductSkuEntity> publishedSkuEntities = productSkus.findByProductId(p.getId()).stream()
                .filter(sku -> ProductSkuEntity.PUBLISHED.equals(sku.getStatus()))
                .toList();
        // Giá storefront đã trừ khuyến mãi sản phẩm (PromotionPricingService) để khớp giá giỏ/checkout:
        // khi có khuyến mãi, salePrice của SKU = giá sau khuyến mãi; listPrice giữ nguyên làm giá gạch.
        List<ProductSkuResponseDto> publishedSkus = publishedSkuEntities.stream()
                .map(sku -> {
                    ProductSkuResponseDto dto = ProductSkuResponseDto.from(sku);
                    BigDecimal price = sku.effectivePrice();
                    BigDecimal discount = price == null ? BigDecimal.ZERO
                            : promotionPricing.unitDiscount(p.getId(), price);
                    if (discount.signum() > 0) {
                        dto.setSalePrice(price.subtract(discount));
                    }
                    return dto;
                })
                .toList();

        BigDecimal priceFrom = publishedSkus.stream()
                .map(dto -> dto.getSalePrice() != null ? dto.getSalePrice() : dto.getListPrice())
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder())
                // Không có SKU nào đang bán (lệch dữ liệu) -> tạm lấy basePrice để không trả null.
                .orElse(p.getBasePrice());

        List<CollectionResponseDto> productCollections = collections.findByProductId(p.getId()).stream()
                .map(CollectionResponseDto::from)
                .toList();

        ProductResponseDto dto = ProductResponseDto.from(p, category != null ? category.getName() : null, priceFrom,
                publishedSkus, productCollections);
        dto.setCategorySlug(category != null ? category.getSlug() : null);
        return dto;
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s.trim();
    }

    // ===================== ADMIN (mục J spec) =====================

    @Transactional(readOnly = true)
    @Override
    public PageResponse<ProductResponseDto> searchForAdmin(String status, String categorySlug, String collectionSlug,
                                                             String keyword, int page, int take) {
        List<ProductEntity> matched = products.searchAdmin(nullToEmpty(status).toUpperCase(),
                nullToEmpty(categorySlug), nullToEmpty(collectionSlug), nullToEmpty(keyword));
        List<ProductResponseDto> dtos = matched.stream()
                .sorted(Comparator.comparing(ProductEntity::getId).reversed())
                .map(this::toAdminDto)
                .toList();
        return PageResponse.paginate(dtos, page, take);
    }

    @Transactional(readOnly = true)
    @Override
    public ProductResponseDto findByIdForAdmin(Long productId) {
        ProductEntity p = requireProduct(productId);
        return toAdminDto(p);
    }

    @Transactional
    @Override
    public ProductResponseDto create(AdminCreateProductRequestDto req) {
        if (products.findByCode(req.getCode().trim()) != null) {
            throw BusinessException.conflict(ErrorCode.DUPLICATE_CODE,
                    "Mã sản phẩm đã tồn tại: " + req.getCode(), req.getCode());
        }
        String slug = (req.getSlug() == null || req.getSlug().isBlank())
                ? SlugUtil.slugify(req.getName()) : SlugUtil.slugify(req.getSlug());
        if (products.findBySlug(slug) != null) {
            throw BusinessException.conflict(ErrorCode.DUPLICATE_SLUG, "Đường dẫn (slug) đã tồn tại: " + slug, slug);
        }
        // Kiểm tra trùng skuCode NGAY TRONG request trước (tránh vòng lặp tạo dở rồi mới phát hiện
        // trùng ở phần tử cuối), rồi mới kiểm tra trùng với DB.
        Set<String> seenCodes = new HashSet<>();
        for (AdminSkuRequestDto v : req.getVariants()) {
            String code = v.getSkuCode().trim();
            if (!seenCodes.add(code.toUpperCase())) {
                throw BusinessException.conflict(ErrorCode.DUPLICATE_SKU,
                        "Mã SKU trùng trong yêu cầu: " + code, code);
            }
            if (productSkus.findBySkuCode(code) != null) {
                throw BusinessException.conflict(ErrorCode.DUPLICATE_SKU, "Mã SKU đã tồn tại: " + code, code);
            }
        }

        ProductEntity p = new ProductEntity(req.getCategoryId(), req.getCode().trim(), req.getName().trim(), slug);
        p.setShortDescription(req.getShortDescription());
        p.setDescription(req.getDescription());
        p.setMaterial(req.getMaterial());
        p.setOccasion(req.getOccasion());
        p.setThumbnailUrl(req.getThumbnailUrl());
        products.create(p);

        List<ProductSkuEntity> createdSkus = new ArrayList<>();
        for (AdminSkuRequestDto v : req.getVariants()) {
            ProductSkuEntity sku = new ProductSkuEntity(v.getSkuCode().trim(), v.getSizeLabel(),
                    v.getListPrice(), v.getOnHand());
            sku.setProductId(p.getId());
            sku.setName(v.getName());
            sku.setMaterial(v.getMaterial());
            sku.setGemstone(v.getGemstone());
            sku.setMetalColorLabel(v.getMetalColorLabel());
            sku.setCaratWeight(v.getCaratWeight());
            sku.setWeightGram(v.getWeightGram());
            sku.setSalePrice(v.getSalePrice());
            sku.setDefault(v.isDefault());
            productSkus.create(sku);
            createdSkus.add(sku);
        }

        return toAdminDto(p, createdSkus);
    }

    @Transactional
    @Override
    public ProductResponseDto update(Long productId, AdminUpdateProductRequestDto req) {
        ProductEntity p = requireProduct(productId);
        p.setCategoryId(req.getCategoryId());
        p.setName(req.getName().trim());
        p.setShortDescription(req.getShortDescription());
        p.setDescription(req.getDescription());
        p.setMaterial(req.getMaterial());
        p.setOccasion(req.getOccasion());
        p.setThumbnailUrl(req.getThumbnailUrl());
        p.touch();
        products.update(p);
        return toAdminDto(p);
    }

    @Transactional
    @Override
    public void softDelete(Long productId) {
        ProductEntity p = requireProduct(productId);
        // deletedId (ai xoá) CHƯA gán được - service không có principal, cần truyền actor id từ Rest
        // qua @AuthenticationPrincipal nếu sau này cần audit chi tiết; để null hiện tại, tương tự
        // cách project đang bỏ ngỏ actor cho vài audit khác (xem OrderTimeline.actor).
        p.setDeletedDate(new Date());
        p.touch();
        products.update(p);
    }

    @Transactional
    @Override
    public ProductResponseDto archive(Long productId) {
        ProductEntity p = requireProduct(productId);
        p.setStatus(ProductEntity.ARCHIVED);
        p.touch();
        products.update(p);
        return toAdminDto(p);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ProductSkuResponseDto> findVariantsByProductId(Long productId) {
        requireProduct(productId);
        return productSkus.findByProductId(productId).stream().map(ProductSkuResponseDto::from).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<ProductSkuResponseDto> searchVariants(String keyword) {
        return productSkus.searchByKeyword(nullToEmpty(keyword)).stream().map(ProductSkuResponseDto::from).toList();
    }

    private ProductEntity requireProduct(Long productId) {
        ProductEntity p = products.findOne(productId);
        if (p == null || p.getDeletedDate() != null) {
            throw new ResourceNotFoundException("Sản phẩm", "Product", productId);
        }
        return p;
    }

    /** Giống {@link #toDto(ProductEntity)} nhưng KHÔNG lọc SKU theo PUBLISHED - admin cần thấy đủ
     * mọi biến thể (kể cả DRAFT/ARCHIVED) khi xem/sửa sản phẩm, khác trang storefront chỉ hiện SKU
     * đang bán. */
    private ProductResponseDto toAdminDto(ProductEntity p) {
        return toAdminDto(p, productSkus.findByProductId(p.getId()));
    }

    private ProductResponseDto toAdminDto(ProductEntity p, List<ProductSkuEntity> skuEntities) {
        CategoryEntity category = p.getCategoryId() != null ? categories.findOne(p.getCategoryId()) : null;

        List<ProductSkuResponseDto> skus = skuEntities.stream().map(ProductSkuResponseDto::from).toList();

        BigDecimal priceFrom = skuEntities.stream()
                .map(ProductSkuEntity::effectivePrice)
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder())
                .orElse(p.getBasePrice());

        List<CollectionResponseDto> productCollections = collections.findByProductId(p.getId()).stream()
                .map(CollectionResponseDto::from)
                .toList();

        ProductResponseDto dto = ProductResponseDto.from(p, category != null ? category.getName() : null, priceFrom,
                skus, productCollections);
        dto.setCategorySlug(category != null ? category.getSlug() : null);
        return dto;
    }
}
