package com.pk.core.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Admin sửa tài khoản (PATCH /admin/users/{id}): field null = giữ nguyên; email rỗng = xoá email; enabled=false = khoá. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserUpdateRequestDto {

    @Size(max = 120)
    private String fullName;

    @Email(message = "Email không hợp lệ")
    @Size(max = 254)
    private String email;

    private Boolean enabled;
}
