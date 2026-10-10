package com.pk.core.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Một dòng giỏ hàng theo spec FE. Mọi id là chuỗi. Giá là số nguyên VND (spec đặt tên "...Minor" nhưng VND
 * không có đơn vị nhỏ hơn đồng nên giá trị = số đồng). Nếu SKU không còn tồn tại thì found=false và các
 * field còn lại (trừ id, variationId, quantity) là null.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartLineResponseDto {

    /** Id dòng giỏ (cart_items.id). */
    private String id;
    private boolean found;
    private String name;
    private String slug;
    private String skuCode;
    /** Trạng thái SKU (DRAFT/PUBLISHED/ARCHIVED). */
    private String status;
    private String productId;
    /** Id SKU. */
    private String variationId;
    private int quantity;
    /** Giá niêm yết (list_price). */
    private Long compareAtPriceAfterTaxMinor;
    /** Giá thực bán (sale_price nếu có, ngược lại list_price). */
    private Long sellingPriceAfterTaxMinor;
    /** Số lượng còn bán được (on_hand - reserved). */
    private Integer stock;
    /** OUT_OF_STOCK / LOW_STOCK / IN_STOCK. */
    private String stockStatus;
    private CartProductResponseDto product;

    // ---- Field tính toán cho FE CartApiItem (không thêm field lưu trữ, không đổi constructor) ----

    private long selling() {
        return sellingPriceAfterTaxMinor == null ? 0L : sellingPriceAfterTaxMinor;
    }

    private boolean discounted() {
        return compareAtPriceAfterTaxMinor != null && sellingPriceAfterTaxMinor != null
                && compareAtPriceAfterTaxMinor > sellingPriceAfterTaxMinor;
    }

    /** CustomerDisplayPrice của FE. */
    public Map<String, Object> getCustomerDisplayPrice() {
        if (!found) {
            return null;
        }
        boolean d = discounted();
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("currency", "VND");
        m.put("compareAtPriceAfterTaxMinor", d ? compareAtPriceAfterTaxMinor : null);
        m.put("sellingPriceAfterTaxMinor", selling());
        m.put("discountPercent", d ? (int) Math.round((compareAtPriceAfterTaxMinor - selling()) * 100.0 / compareAtPriceAfterTaxMinor) : null);
        m.put("hasDiscount", d);
        return m;
    }

    public Long getDisplayPriceAfterTaxMinor() {
        return found ? selling() : null;
    }

    public Long getUnitPromotionDiscountMinor() {
        return found ? (discounted() ? compareAtPriceAfterTaxMinor - selling() : 0L) : null;
    }

    public Long getLineSellingSubtotalMinor() {
        return found ? selling() * quantity : null;
    }

    public Long getLineDisplaySubtotalMinor() {
        return getLineSellingSubtotalMinor();
    }

    public Integer getAvailableStock() {
        return stock;
    }

    /** IN_STOCK | OUT_OF_STOCK (FE không phân biệt LOW_STOCK). */
    public String getAvailabilityCode() {
        return found ? ("OUT_OF_STOCK".equals(stockStatus) ? "OUT_OF_STOCK" : "IN_STOCK") : null;
    }

    public Boolean getIsValid() {
        return found && stock != null && stock >= quantity;
    }

    public String getReason() {
        if (!found) {
            return "NOT_FOUND";
        }
        if (stock == null || stock <= 0) {
            return "OUT_OF_STOCK";
        }
        return stock < quantity ? "INSUFFICIENT_STOCK" : null;
    }

    public List<Map<String, Object>> getMedia() {
        List<Map<String, Object>> list = new ArrayList<>();
        if (product != null && product.getImage() != null) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("url", product.getImage());
            m.put("sortOrder", 0);
            list.add(m);
        }
        return list;
    }

    public List<Object> getAttributes() {
        return new ArrayList<>();
    }

    public List<Object> getGifts() {
        return new ArrayList<>();
    }
}
