package com.pk.core.model.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.pk.core.common.dto.BaseDto;
import com.pk.core.model.entity.ProductEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDto extends BaseDto {

    /** FE dùng id dạng chuỗi ("12"); kiểu Java vẫn là Long. */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;
    private String code;
    private String name;
    private String slug;
    private String shortDescription;
    private String description;
    private String status;
    private Long categoryId;
    private String categoryName;
    private String material;
    private String occasion;
    private String thumbnailUrl;
    /** Giá thấp nhất trong các SKU còn bán (PUBLISHED) - tính lại từ skus, không lấy thẳng cột basePrice. */
    private BigDecimal priceFrom;
    private List<ProductSkuResponseDto> skus;
    private List<CollectionResponseDto> collections;
    private Date publishedAt;
    /** Slug danh mục (service gán sau khi dựng DTO) - dùng cho field FE `category.slug`. */
    private String categorySlug;

    public static ProductResponseDto from(ProductEntity p, String categoryName, BigDecimal priceFrom,
                                           List<ProductSkuResponseDto> skus, List<CollectionResponseDto> collections) {
        return BaseDto.of(new ProductResponseDto(p.getId(), p.getCode(), p.getName(), p.getSlug(),
                p.getShortDescription(), p.getDescription(), p.getStatus(), p.getCategoryId(), categoryName,
                p.getMaterial(), p.getOccasion(), p.getThumbnailUrl(), priceFrom, skus, collections,
                p.getPublishedAt(), null), p);
    }

    // =====================================================================================================
    // Field theo đúng kiểu dữ liệu FE storefront (jewelry-ecommerce-client: IProductSkuCardItem,
    // IProductBySlugResponse, ApiProduct). Chỉ là field tính thêm từ dữ liệu đã có - các field cũ giữ nguyên.
    // =====================================================================================================

    private static final String BRAND_NAME = "Jewelry Ecommerce";
    private static final String BRAND_CODE = "JEWELRY";

    private ProductSkuResponseDto defaultSku() {
        if (skus == null || skus.isEmpty()) {
            return null;
        }
        for (ProductSkuResponseDto s : skus) {
            if (s.isDefault()) {
                return s;
            }
        }
        return skus.get(0);
    }

    private static long minor(BigDecimal v) {
        return v == null ? 0L : v.setScale(0, RoundingMode.HALF_UP).longValue();
    }

    /** CustomerDisplayPrice của FE: giá bán sau KM; compareAt chỉ có khi đang giảm (listPrice > giá bán). */
    private static Map<String, Object> displayPrice(ProductSkuResponseDto s) {
        BigDecimal list = s == null ? null : s.getListPrice();
        BigDecimal sale = s == null ? null : s.getSalePrice();
        long selling = minor(sale != null ? sale : list);
        boolean discounted = sale != null && list != null && list.compareTo(sale) > 0;
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("currency", "VND");
        m.put("compareAtPriceAfterTaxMinor", discounted ? minor(list) : null);
        m.put("sellingPriceAfterTaxMinor", selling);
        m.put("discountPercent", discounted ? (int) Math.round((minor(list) - selling) * 100.0 / minor(list)) : null);
        m.put("hasDiscount", discounted);
        return m;
    }

    private static String feStockStatus(ProductSkuResponseDto s) {
        // FE chỉ phân biệt còn/hết hàng; LOW_STOCK vẫn mua được.
        return s == null || "OUT_OF_STOCK".equals(s.getStockStatus()) ? "OUT_OF_STOCK" : "IN_STOCK";
    }

    private Map<String, Object> pricingOf(ProductSkuResponseDto s) {
        Map<String, Object> cdp = displayPrice(s);
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("currency", "VND");
        m.put("displayPriceAfterTaxMinor", cdp.get("sellingPriceAfterTaxMinor"));
        m.put("sellingPriceAfterTaxMinor", cdp.get("sellingPriceAfterTaxMinor"));
        m.put("compareAtPriceAfterTaxMinor", cdp.get("compareAtPriceAfterTaxMinor"));
        m.put("discountPercent", cdp.get("discountPercent"));
        m.put("customerDisplayPrice", cdp);
        // Dạng cũ ApiProduct.pricing (chuỗi) cho trang tìm kiếm/yêu thích.
        m.put("minDisplayPriceAfterTaxMinor", String.valueOf(cdp.get("sellingPriceAfterTaxMinor")));
        m.put("minSellingPriceAfterTaxMinor", String.valueOf(cdp.get("sellingPriceAfterTaxMinor")));
        m.put("minCompareAtPriceAfterTaxMinor", String.valueOf(cdp.get("compareAtPriceAfterTaxMinor") == null ? 0 : cdp.get("compareAtPriceAfterTaxMinor")));
        return m;
    }

    private static String iso(Date d) {
        return d == null ? null : d.toInstant().toString();
    }

    /** IProductSkuCardItem.productId / ApiProduct.productId. */
    public String getProductId() {
        return id == null ? null : String.valueOf(id);
    }

    public String getBrandName() {
        return BRAND_NAME;
    }

    public boolean getIsPurchasable() {
        return ProductEntity.PUBLISHED.equals(status) && skus != null && !skus.isEmpty();
    }

    /** SKU đang chọn (mặc định) cho card danh sách. */
    public Map<String, Object> getSelectedSku() {
        ProductSkuResponseDto s = defaultSku();
        if (s == null) {
            return null;
        }
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", String.valueOf(s.getId()));
        m.put("skuCode", s.getSkuCode());
        m.put("image", thumbnailUrl);
        m.put("imageHover", thumbnailUrl);
        m.put("customerDisplayPrice", displayPrice(s));
        m.put("stockStatus", feStockStatus(s));
        return m;
    }

    @JsonInclude(JsonInclude.Include.ALWAYS)
    public Object getVisualSwitch() {
        return null;
    }

    public Map<String, Object> getAddToCart() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("mode", skus != null && skus.size() > 1 ? "SELECT_REQUIRED" : "DIRECT");
        return m;
    }

    public Map<String, Object> getPricePresentation() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("showDiscountPercent", true);
        return m;
    }

    public String getUpdatedAt() {
        Date d = getUpdatedDate() != null ? getUpdatedDate() : (publishedAt != null ? publishedAt : getCreatedDate());
        return iso(d);
    }

    public String getCreatedAt() {
        return iso(getCreatedDate() != null ? getCreatedDate() : publishedAt);
    }

    // ---- chi tiết sản phẩm (IProductBySlugResponse) ----

    public String getSku() {
        ProductSkuResponseDto s = defaultSku();
        return s == null ? null : s.getSkuCode();
    }

    public String getSpuCode() {
        return code;
    }

    public Map<String, Object> getBrand() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("name", BRAND_NAME);
        m.put("code", BRAND_CODE);
        m.put("image", null);
        m.put("tenantCode", BRAND_CODE);
        return m;
    }

    public Map<String, Object> getCategory() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", categoryId == null ? null : String.valueOf(categoryId));
        m.put("name", categoryName);
        m.put("slug", categorySlug);
        return m;
    }

    public List<Map<String, Object>> getGallery() {
        List<Map<String, Object>> list = new ArrayList<>();
        if (thumbnailUrl != null && !thumbnailUrl.isBlank()) {
            Map<String, Object> g = new LinkedHashMap<>();
            g.put("url", thumbnailUrl);
            g.put("type", "IMAGE");
            g.put("sortOrder", 0);
            g.put("isPrimary", true);
            list.add(g);
        }
        return list;
    }

    public Map<String, Object> getSeo() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("metaTitle", name);
        m.put("metaDescription", shortDescription);
        m.put("metaKeywords", null);
        m.put("image", thumbnailUrl);
        return m;
    }

    public Map<String, Object> getDimensions() {
        ProductSkuResponseDto s = defaultSku();
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("weight", s == null || s.getWeightGram() == null ? null : s.getWeightGram().doubleValue());
        m.put("length", null);
        m.put("width", null);
        m.put("height", null);
        return m;
    }

    /** Vừa là IProductPricing (chi tiết) vừa chứa các field min* của ApiProduct.pricing (tìm kiếm/yêu thích). */
    public Map<String, Object> getPricing() {
        return pricingOf(defaultSku());
    }

    /** ApiProduct.defaultDisplay (dạng chuỗi). */
    public Map<String, Object> getDefaultDisplay() {
        Map<String, Object> cdp = displayPrice(defaultSku());
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("displayPriceAfterTaxMinor", String.valueOf(cdp.get("sellingPriceAfterTaxMinor")));
        m.put("sellingPriceAfterTaxMinor", String.valueOf(cdp.get("sellingPriceAfterTaxMinor")));
        m.put("compareAtPriceAfterTaxMinor", String.valueOf(cdp.get("compareAtPriceAfterTaxMinor") == null ? 0 : cdp.get("compareAtPriceAfterTaxMinor")));
        m.put("image", thumbnailUrl == null ? "" : thumbnailUrl);
        m.put("customerDisplayPrice", cdp);
        return m;
    }

    public Map<String, Object> getAvailability() {
        boolean inStock = false;
        if (skus != null) {
            for (ProductSkuResponseDto s : skus) {
                if (!"OUT_OF_STOCK".equals(s.getStockStatus())) {
                    inStock = true;
                    break;
                }
            }
        }
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("inStock", inStock);
        m.put("stockStatus", inStock ? "IN_STOCK" : "OUT_OF_STOCK");
        return m;
    }

    public Map<String, Object> getDefaultVariant() {
        ProductSkuResponseDto s = defaultSku();
        return s == null ? null : variantOf(s);
    }

    public String getDefaultVariantId() {
        ProductSkuResponseDto s = defaultSku();
        return s == null ? null : String.valueOf(s.getId());
    }

    public List<Map<String, Object>> getVariants() {
        List<Map<String, Object>> list = new ArrayList<>();
        if (skus != null) {
            for (ProductSkuResponseDto s : skus) {
                list.add(variantOf(s));
            }
        }
        return list;
    }

    private static final String[][] ATTRS = {
            {"size", "Kích cỡ"}, {"material", "Chất liệu"}, {"gemstone", "Đá chủ"}, {"metal_color", "Màu kim loại"}};

    private static String attrValue(ProductSkuResponseDto s, int idx) {
        String v = switch (idx) {
            case 0 -> s.getSizeLabel();
            case 1 -> s.getMaterial();
            case 2 -> s.getGemstone();
            default -> s.getMetalColorLabel();
        };
        return v == null || v.isBlank() ? null : v;
    }

    private static Map<String, Object> attributeDef(int idx) {
        Map<String, Object> a = new LinkedHashMap<>();
        a.put("id", ATTRS[idx][0]);
        a.put("name", ATTRS[idx][1]);
        a.put("code", ATTRS[idx][0]);
        a.put("index", idx);
        a.put("displayType", "TEXT");
        return a;
    }

    /** IProductVariation: mỗi SKU của sản phẩm là một biến thể. */
    private Map<String, Object> variantOf(ProductSkuResponseDto s) {
        Map<String, Object> v = new LinkedHashMap<>();
        v.put("id", String.valueOf(s.getId()));
        v.put("slug", slug);
        v.put("name", s.getName() != null && !s.getName().isBlank() ? s.getName() : name);
        v.put("sku", s.getSkuCode());
        v.put("stock", s.getAvailableStock());
        v.put("stockStatus", feStockStatus(s));
        v.put("isDefault", s.isDefault());
        v.put("image", thumbnailUrl == null ? "" : thumbnailUrl);
        v.put("gallery", getGallery());
        List<Map<String, Object>> values = new ArrayList<>();
        Map<String, String> flat = new LinkedHashMap<>();
        for (int i = 0; i < ATTRS.length; i++) {
            String val = attrValue(s, i);
            if (val == null) {
                continue;
            }
            Map<String, Object> av = new LinkedHashMap<>();
            av.put("id", ATTRS[i][0] + ":" + val);
            av.put("code", val);
            av.put("value", val);
            av.put("image", null);
            av.put("attribute", attributeDef(i));
            values.add(av);
            flat.put(ATTRS[i][0], val);
        }
        v.put("attributeValues", values);
        v.put("attributes", flat);
        v.put("pricing", pricingOf(s));
        return v;
    }

    /** Bộ chọn biến thể: chỉ thuộc tính có từ 2 giá trị khác nhau trở lên mới cần chọn. */
    public List<Map<String, Object>> getVariantSelectors() {
        List<Map<String, Object>> selectors = new ArrayList<>();
        if (skus == null || skus.size() < 2) {
            return selectors;
        }
        ProductSkuResponseDto def = defaultSku();
        for (int i = 0; i < ATTRS.length; i++) {
            Set<String> distinct = new LinkedHashSet<>();
            for (ProductSkuResponseDto s : skus) {
                String val = attrValue(s, i);
                if (val != null) {
                    distinct.add(val);
                }
            }
            if (distinct.size() < 2) {
                continue;
            }
            String selected = def == null ? null : attrValue(def, i);
            List<Map<String, Object>> options = new ArrayList<>();
            for (String val : distinct) {
                List<String> ids = new ArrayList<>();
                boolean available = false;
                for (ProductSkuResponseDto s : skus) {
                    if (val.equals(attrValue(s, i))) {
                        ids.add(String.valueOf(s.getId()));
                        available = available || !"OUT_OF_STOCK".equals(s.getStockStatus());
                    }
                }
                Map<String, Object> o = new LinkedHashMap<>();
                o.put("id", ATTRS[i][0] + ":" + val);
                o.put("code", val);
                o.put("label", val);
                o.put("value", val);
                o.put("thumbnail", null);
                o.put("selected", val.equals(selected));
                o.put("available", available);
                o.put("variationIds", ids);
                options.add(o);
            }
            Map<String, Object> sel = new LinkedHashMap<>();
            sel.put("attribute", attributeDef(i));
            sel.put("options", options);
            selectors.add(sel);
        }
        return selectors;
    }

    public List<Object> getProductInfos() {
        return new ArrayList<>();
    }

    // ---- ApiProduct (tìm kiếm / yêu thích) ----

    public String getProductName() {
        return name;
    }

    public String getProductSlug() {
        return slug;
    }

    public String getProductStatus() {
        return status;
    }

    public String getImage() {
        return thumbnailUrl == null ? "" : thumbnailUrl;
    }

    public String getImageHover() {
        return getImage();
    }

    public boolean getRequiresSelectionDialog() {
        return skus != null && skus.size() > 1;
    }

    public String getStockStatus() {
        return String.valueOf(getAvailability().get("stockStatus"));
    }

    public String getDefaultVariationId() {
        return getDefaultVariantId();
    }
}
