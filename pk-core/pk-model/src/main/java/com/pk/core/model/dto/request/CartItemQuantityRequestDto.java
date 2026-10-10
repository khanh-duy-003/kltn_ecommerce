package com.pk.core.model.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Body cho PUT /storefront/cart/cart: đặt SỐ LƯỢNG CUỐI của một dòng (khác POST: cộng dồn). quantity = 0 là xoá dòng. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItemQuantityRequestDto {
    /** Id SKU (FE gọi SKU là "variation"). */
    @NotBlank
    private String variationId;
    @Min(0)
    private int quantity;
}
