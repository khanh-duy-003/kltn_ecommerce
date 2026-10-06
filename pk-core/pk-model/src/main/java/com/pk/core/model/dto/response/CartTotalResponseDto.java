package com.pk.core.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/** Kết quả POST /storefront/cart/cart/calculate-total (số nguyên VND). */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartTotalResponseDto {

    private long subTotal;
    private long discountTotal;
    private List<CartDiscountResponseDto> discounts;
    private long shippingFee;
    private long totalAmount;
    private long rewardPoints;
}
