package com.pk.core.model.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/** POST/DELETE /storefront/product/customer/wishlist. productIds là chuỗi số (id sản phẩm) theo spec FE. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WishlistRequestDto {

    @NotEmpty
    private List<@Pattern(regexp = "^[0-9]{1,18}$", message = "productId phải là số") String> productIds;
}
