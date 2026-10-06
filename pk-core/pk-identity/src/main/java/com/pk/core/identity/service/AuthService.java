package com.pk.core.identity.service;

import com.pk.core.model.dto.request.LoginRequestDto;
import com.pk.core.model.dto.request.RegisterRequestDto;
import com.pk.core.model.dto.request.UpdateProfileRequestDto;
import com.pk.core.model.dto.response.TokenResponseDto;
import com.pk.core.model.dto.response.UserResponseDto;

public interface AuthService {

    TokenResponseDto register(RegisterRequestDto req);

    TokenResponseDto login(LoginRequestDto req);

    /** Đăng nhập ADMIN (POST /admin/auth/login, gói api.rest.admin trong pk-api - xem AdminAuthRest). Kiểm tra
     * SĐT/mật khẩu giống {@link #login(LoginRequestDto)}, THÊM bước bắt buộc role ADMIN: nếu tài
     * khoản đúng SĐT/mật khẩu nhưng không có role ADMIN thì ném 403 FORBIDDEN (không phải 401, vì
     * danh tính đã xác thực đúng, chỉ là không đủ quyền). Thêm 2026-09-29 theo rule "admin tách
     * riêng hoàn toàn" - xem RULE-CODE.md. */
    TokenResponseDto loginAdmin(LoginRequestDto req);

    TokenResponseDto refresh(String refreshToken);

    void logout(String refreshToken);

    UserResponseDto me(Long userId);

    /** Sửa hồ sơ (tên, email) - KHÔNG phải cơ chế authen (register/login/refresh/logout), chỉ cập
     * nhật thông tin hiển thị. Theo document/09-tong-hop-api-fe.md mục B: PUT /storefront/me. */
    UserResponseDto updateProfile(Long userId, UpdateProfileRequestDto req);
}
