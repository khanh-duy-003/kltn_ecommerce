package com.pk.core.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/** Admin tạo tài khoản (POST /admin/users): SĐT là định danh đăng nhập, email tuỳ chọn, `roles` thuộc
 * ADMIN | CATALOG_MANAGER | ORDER_MANAGER | CUSTOMER. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserCreateRequestDto {

    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 120)
    private String fullName;

    @NotBlank(message = "Số điện thoại không được để trống")
    private String phone;

    @Email(message = "Email không hợp lệ")
    @Size(max = 254)
    private String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 8, max = 72, message = "Mật khẩu phải từ 8 đến 72 ký tự")
    private String password;

    @NotEmpty(message = "Phải gán ít nhất 1 vai trò")
    private List<String> roles;
}
