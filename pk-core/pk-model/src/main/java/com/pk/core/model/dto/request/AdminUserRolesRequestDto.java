package com.pk.core.model.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/** Gán vai trò cho tài khoản (PUT /admin/users/{id}/roles): THAY TOÀN BỘ danh sách vai trò hiện có. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserRolesRequestDto {

    @NotEmpty(message = "Phải gán ít nhất 1 vai trò")
    private List<String> roles;
}
