package com.pk.core.model.dto.request;

import com.pk.core.common.util.PhoneUtil;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** POST /storefront/customer-request/order-support: khách xin hỗ trợ về một đơn hàng. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderSupportRequestDto {

    @NotBlank
    @Pattern(regexp = PhoneUtil.INPUT_REGEX, message = "Số điện thoại không hợp lệ")
    private String phone;

    @NotBlank
    @Size(max = 60)
    private String orderCode;
}
