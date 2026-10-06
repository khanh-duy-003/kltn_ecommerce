package com.pk.core.model.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/** POST /storefront/cart/cart/calculate-total. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartTotalRequestDto {

    @NotEmpty
    @Valid
    private List<CartTotalItemRequestDto> items;
}
