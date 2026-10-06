package com.pk.core.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/** Kết quả GET /storefront/cart/cart/recommendation. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartRecommendationResponseDto {

    private long total;
    private List<CartRecommendationItemResponseDto> list;
    private PaginationResponseDto pagination;
}
