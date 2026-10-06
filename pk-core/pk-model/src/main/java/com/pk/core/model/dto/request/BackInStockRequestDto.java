package com.pk.core.model.dto.request;

import com.pk.core.common.util.PhoneUtil;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** POST /storefront/customer-request/back-in-stock: báo cho khách khi SKU có hàng lại. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BackInStockRequestDto {

    @NotBlank
    @Pattern(regexp = PhoneUtil.INPUT_REGEX, message = "Số điện thoại không hợp lệ")
    private String phone;

    @NotBlank
    @Size(max = 64)
    private String skuCode;
}
