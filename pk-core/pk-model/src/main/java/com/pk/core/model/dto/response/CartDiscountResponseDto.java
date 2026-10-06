package com.pk.core.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Một khoản giảm giá trong {@link CartTotalResponseDto}. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartDiscountResponseDto {

    private String label;
    private long value;
}
