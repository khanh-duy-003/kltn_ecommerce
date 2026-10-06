package com.pk.core.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Sản phẩm gợi ý trong giỏ (đại diện bởi một SKU của sản phẩm). sellingPriceAfterTaxMinor là CHUỖI theo spec FE. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartRecommendationItemResponseDto {

    /** Id SKU đại diện. */
    private String id;
    private String slug;
    private String name;
    private String productId;
    private String sellingPriceAfterTaxMinor;
    private int stock;
    private String status;
}
