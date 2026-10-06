package com.pk.core.model.dto.request;

import com.pk.core.common.util.PhoneUtil;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** POST /storefront/auth/register/send-otp và /storefront/auth/forgot-password (cùng body theo spec FE). */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SendOtpRequestDto {

    @NotBlank
    @Pattern(regexp = PhoneUtil.INPUT_REGEX, message = "Số điện thoại không hợp lệ")
    private String phone;
}
