package com.pk.core.identity.rest;

import lombok.RequiredArgsConstructor;

import com.pk.core.common.web.BaseRes;
import com.pk.core.model.constant.UrlIdentConstant;
import com.pk.core.identity.service.AuthService;
import com.pk.core.business.web.AbstractRest;  // gộp dùng chung với pk-api, không còn bản riêng ở identity

import com.pk.core.model.dto.request.LoginRequestDto;
import com.pk.core.model.dto.request.RefreshRequestDto;
import com.pk.core.model.dto.request.RegisterRequestDto;
import com.pk.core.model.dto.request.UpdateProfileRequestDto;
import com.pk.core.identity.security.model.SecurityUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthRest extends AbstractRest {

    private final AuthService authService;


    @PostMapping(UrlIdentConstant.Auth.BASE + "/register")
    @ResponseStatus(HttpStatus.CREATED)
    public BaseRes register(@Valid @RequestBody RegisterRequestDto request,
                             HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            return restSuccessHandle.handleSuccess(authService.register(request));
        } catch (Exception ex) {
            return restErrorHandle.handleException(ex, httpRequest, httpResponse);
        }
    }

    @PostMapping(UrlIdentConstant.Auth.BASE + "/login")
    public BaseRes login(@Valid @RequestBody LoginRequestDto request,
                          HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            return restSuccessHandle.handleSuccess(authService.login(request));
        } catch (Exception ex) {
            return restErrorHandle.handleException(ex, httpRequest, httpResponse);
        }
    }

    @PostMapping(UrlIdentConstant.Auth.BASE + "/refresh")
    public BaseRes refresh(@Valid @RequestBody RefreshRequestDto request,
                            HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            return restSuccessHandle.handleSuccess(authService.refresh(request.getRefreshToken()));
        } catch (Exception ex) {
            return restErrorHandle.handleException(ex, httpRequest, httpResponse);
        }
    }

    /** 204 No Content: không có body nên không bọc BaseRes; lỗi (nếu có) rơi vào GlobalExceptionHandler
     * của pk-business (component-scan toàn app, xem PkServiceApplication) làm lưới an toàn. */
    @PostMapping(UrlIdentConstant.Auth.BASE + "/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@Valid @RequestBody RefreshRequestDto request) {
        authService.logout(request.getRefreshToken());
    }

    @GetMapping(UrlIdentConstant.Common.ME)
    public BaseRes me(@AuthenticationPrincipal SecurityUser principal,
                       HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            return restSuccessHandle.handleSuccess(authService.me(principal.getId()));
        } catch (Exception ex) {
            return restErrorHandle.handleException(ex, httpRequest, httpResponse);
        }
    }

    /** Sửa hồ sơ (tên, email) - KHÔNG phải cơ chế authen, chỉ cập nhật thông tin hiển thị. */
    @PutMapping(UrlIdentConstant.Common.ME)
    public BaseRes updateMe(@AuthenticationPrincipal SecurityUser principal,
                             @Valid @RequestBody UpdateProfileRequestDto request,
                             HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            return restSuccessHandle.handleSuccess(authService.updateProfile(principal.getId(), request));
        } catch (Exception ex) {
            return restErrorHandle.handleException(ex, httpRequest, httpResponse);
        }
    }
}
