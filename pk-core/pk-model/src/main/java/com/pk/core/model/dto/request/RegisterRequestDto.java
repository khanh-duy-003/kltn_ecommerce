package com.pk.core.model.dto.request;

import com.pk.core.common.util.PhoneUtil;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDto {

    /** Tuỳ chọn: chỉ lưu thêm thông tin liên hệ, không dùng để đăng nhập. Bỏ trống/null = không có email. */
    @Email
    @Size(max = 254)
    private String email;

    @NotBlank
    @Size(min = 8, max = 72, message = "Mật khẩu phải từ 8 đến 72 ký tự")
    private String password;

    @NotBlank
    @Size(max = 120)
    private String fullName;

    /** Bắt buộc - là định danh đăng nhập. Dạng nhập: 0xxxxxxxxx, 84xxxxxxxxx hoặc +84xxxxxxxxx. */
    @NotBlank
    @Pattern(regexp = PhoneUtil.INPUT_REGEX, message = "Số điện thoại không hợp lệ")
    private String phone;
}
