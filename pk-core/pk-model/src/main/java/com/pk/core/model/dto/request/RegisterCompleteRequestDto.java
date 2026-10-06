package com.pk.core.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * POST /storefront/auth/register/complete. Theo spec FE: email BẮT BUỘC ở bước này (khác
 * RegisterRequestDto của /api/auth/register, nơi email tuỳ chọn). SĐT lấy từ registrationToken, không gửi lại.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterCompleteRequestDto {

    @NotBlank
    private String registrationToken;

    @NotBlank
    @Size(min = 2, max = 100)
    private String fullName;

    @NotBlank
    @Email
    @Size(max = 254)
    private String email;

    @NotBlank
    @Size(min = 8, max = 72, message = "Mật khẩu phải từ 8 đến 72 ký tự")
    private String password;
}
