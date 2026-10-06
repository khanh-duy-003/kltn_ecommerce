package com.pk.core.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Kết quả POST /storefront/auth/register/verify-otp: token dùng một lần cho register/complete. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OtpVerificationResponseDto {

    private String registrationToken;

    private long expiresInSeconds;
}
