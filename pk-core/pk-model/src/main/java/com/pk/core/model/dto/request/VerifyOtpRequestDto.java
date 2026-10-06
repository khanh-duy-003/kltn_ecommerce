package com.pk.core.model.dto.request;

import com.pk.core.common.util.PhoneUtil;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** POST /storefront/auth/register/verify-otp. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VerifyOtpRequestDto {

    @NotBlank
    @Pattern(regexp = PhoneUtil.INPUT_REGEX, message = "Số điện thoại không hợp lệ")
    private String phone;

    @NotBlank
    @Pattern(regexp = "^[0-9]{6}$", message = "Mã OTP gồm 6 chữ số")
    private String otp;
}
