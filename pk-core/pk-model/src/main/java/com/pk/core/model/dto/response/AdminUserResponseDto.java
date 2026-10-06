package com.pk.core.model.dto.response;

import com.pk.core.model.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/** Một tài khoản (khách hoặc admin) kèm vai trò, cho GET /admin/users. KHÔNG bao giờ chứa passwordHash. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserResponseDto {

    private Long id;
    private String phone;
    private String email;
    private String fullName;
    private boolean enabled;
    private List<String> roles;
    private Date createdAt;

    public static AdminUserResponseDto from(UserEntity u, List<String> roles) {
        return new AdminUserResponseDto(u.getId(), u.getPhone(), u.getEmail(), u.getFullName(), u.isEnabled(), roles,
                u.getCreatedDate());
    }
}
