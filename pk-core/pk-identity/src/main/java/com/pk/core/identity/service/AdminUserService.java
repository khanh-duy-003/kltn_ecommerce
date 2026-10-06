package com.pk.core.identity.service;

import com.pk.core.model.dto.request.AdminUserCreateRequestDto;
import com.pk.core.model.dto.request.AdminUserRolesRequestDto;
import com.pk.core.model.dto.request.AdminUserUpdateRequestDto;
import com.pk.core.model.dto.response.AdminUserResponseDto;
import com.pk.core.model.dto.response.RoleResponseDto;

import java.util.List;

/** Quản lý tài khoản, vai trò và quyền (chỉ ADMIN). Quyền chi tiết do vai trò mang (xem Permission). Đặt ở pk-identity vì
 * cần PasswordEncoder, RefreshTokenService và bảng quyền {@code Permission}. */
public interface AdminUserService {

    AdminUserResponseDto create(AdminUserCreateRequestDto req);

    AdminUserResponseDto findById(Long id);

    /** PATCH: sửa họ tên/email, khoá-mở khoá. Không cho tự khoá chính mình. Khoá tài khoản thu hồi mọi refresh token. */
    AdminUserResponseDto update(Long actorId, Long id, AdminUserUpdateRequestDto req);

    /** Gán vai trò (thay toàn bộ). Không cho tự gỡ ADMIN của chính mình. Thu hồi refresh token để vai trò mới có hiệu lực. */
    AdminUserResponseDto assignRoles(Long actorId, Long id, AdminUserRolesRequestDto req);

    /** Danh sách vai trò kèm quyền chi tiết. */
    List<RoleResponseDto> listRoles();
}
