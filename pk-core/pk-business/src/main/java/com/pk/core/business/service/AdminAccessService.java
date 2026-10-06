package com.pk.core.business.service;

import com.pk.core.model.dto.response.AdminUserResponseDto;
import com.pk.core.model.dto.response.RoleResponseDto;

import java.util.List;

/** Tài khoản và phân quyền theo spec FE (GET /admin/users, GET /admin/roles) - CHỈ đọc. Thêm 2026-10-06. */
public interface AdminAccessService {

    /** Mọi tài khoản chưa xoá mềm (khách + admin) kèm vai trò, theo id tăng dần. Không lộ passwordHash. */
    List<AdminUserResponseDto> listUsers();

    /** Mọi vai trò (ADMIN, CUSTOMER...), theo id tăng dần. */
    List<RoleResponseDto> listRoles();
}
