package com.pk.core.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
}
