package com.pk.core.api.rest.admin;

import lombok.RequiredArgsConstructor;

import com.pk.core.model.constant.admin.UrlAdminConstant;
import com.pk.core.common.web.BaseRes;
import com.pk.core.business.web.AbstractRest;
import com.pk.core.identity.service.AuthService;
import com.pk.core.identity.security.model.SecurityUser;
import com.pk.core.model.dto.request.LoginRequestDto;
import com.pk.core.model.dto.request.RefreshRequestDto;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * API admin — Auth (document/09-tong-hop-api-fe.md mục I). Gói `api.rest.admin` (module pk-api), TÁCH RIÊNG khỏi Rest storefront (gói `api.rest`)
 * (yêu cầu người dùng 2026-09-29, xem claude/RULE-CODE.md). Dùng lại cơ chế JWT/RefreshToken sẵn có ở
 * pk-identity (AuthService) - chỉ khác login(): {@link AuthService#loginAdmin} bắt buộc thêm role
 * ADMIN. logout/me dùng lại nguyên method identity đã có (đăng nhập thường và đăng nhập admin ra cùng
 * 1 loại JWT/refresh token, không có gì để phân biệt "phiên admin" so với "phiên khách hàng" ở tầng
 * lưu trữ - việc giới hạn chỉ ADMIN mới gọi được nằm ở SecurityConfig: hasRole("ADMIN") cho cả
 * UrlAdminConstant.Common.BASE + "/**", trừ route login).
 *
 * <p>Đối tượng User trả về: dùng lại {@code UserResponseDto} (giống /api/me của storefront) - CHƯA
 * khớp 100% object "User (admin)" trong spec (`{id, fullName, email, phone, avatarUrl, role,
 * loyaltyPoints, createdAt}`): thiếu {@code avatarUrl}/{@code loyaltyPoints} vì bảng {@code users}
 * hiện chưa có 2 cột này, và trả {@code roles} (mảng) thay vì {@code role} (số ít) vì UserEntity vốn
 * hỗ trợ nhiều role/user. Cố ý CHƯA tạo migration mới hay DTO admin riêng cho sai khác nhỏ này - có
 * thể bổ sung khi có yêu cầu cụ thể.</p>
 */
@RestController
@RequiredArgsConstructor
public class AdminAuthRest extends AbstractRest {

    private final AuthService authService;

    @PostMapping(UrlAdminConstant.Auth.LOGIN)
    public BaseRes login(@Valid @RequestBody LoginRequestDto request,
                          HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            return restSuccessHandle.handleSuccess(authService.loginAdmin(request));
        } catch (Exception ex) {
            return restErrorHandle.handleException(ex, httpRequest, httpResponse);
        }
    }

    /** 204 No Content: không có body nên không bọc AbstractRest/try-catch, giống AuthRest.logout của
     * storefront - lỗi (nếu có) rơi vào GlobalExceptionHandler (component-scan toàn app) làm lưới an
     * toàn. */
    @PostMapping(UrlAdminConstant.Auth.LOGOUT)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@Valid @RequestBody RefreshRequestDto request) {
        authService.logout(request.getRefreshToken());
    }

    @GetMapping(UrlAdminConstant.Auth.ME)
    public BaseRes me(@AuthenticationPrincipal SecurityUser principal,
                       HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            return restSuccessHandle.handleSuccess(authService.me(principal.getId()));
        } catch (Exception ex) {
            return restErrorHandle.handleException(ex, httpRequest, httpResponse);
        }
    }
}
