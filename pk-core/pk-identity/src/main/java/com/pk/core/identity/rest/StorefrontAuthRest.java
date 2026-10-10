package com.pk.core.identity.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import com.pk.core.model.constant.UrlConstant;
import lombok.RequiredArgsConstructor;

import com.pk.core.business.web.AbstractRest;
import com.pk.core.common.web.BaseRes;
import com.pk.core.identity.service.PhoneAuthService;
import com.pk.core.model.constant.UrlIdentConstant;
import com.pk.core.model.dto.request.RegisterCompleteRequestDto;
import com.pk.core.model.dto.request.ResetPasswordRequestDto;
import com.pk.core.model.dto.request.ResolvePhoneRequestDto;
import com.pk.core.model.dto.request.SendOtpRequestDto;
import com.pk.core.model.dto.request.VerifyOtpRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Luồng SĐT + OTP theo spec FE (/storefront/auth/...). Các endpoint login/logout/me theo spec FE vẫn là
 * route cũ trong {@link AuthRest} (/api/auth/..., /api/me) - không đổi ở đây.
 */
@RestController
@RequestMapping(UrlConstant.Common.API + UrlConstant.Common.VERSION)
@RequiredArgsConstructor
public class StorefrontAuthRest extends AbstractRest {

    private final PhoneAuthService phoneAuthService;

    @PostMapping(UrlIdentConstant.StorefrontAuth.BASE + "/resolve-phone")
    public BaseRes resolvePhone(@Valid @RequestBody ResolvePhoneRequestDto request,
                                HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            return restSuccessHandle.handleSuccess(phoneAuthService.resolvePhone(request.getPhone()));
        } catch (Exception ex) {
            return restErrorHandle.handleException(ex, httpRequest, httpResponse);
        }
    }

    @PostMapping(UrlIdentConstant.StorefrontAuth.BASE + "/register/send-otp")
    public BaseRes sendRegisterOtp(@Valid @RequestBody SendOtpRequestDto request,
                                   HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            return restSuccessHandle.handleSuccess(phoneAuthService.sendRegisterOtp(request.getPhone()));
        } catch (Exception ex) {
            return restErrorHandle.handleException(ex, httpRequest, httpResponse);
        }
    }

    @PostMapping(UrlIdentConstant.StorefrontAuth.BASE + "/register/verify-otp")
    public BaseRes verifyRegisterOtp(@Valid @RequestBody VerifyOtpRequestDto request,
                                     HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            return restSuccessHandle.handleSuccess(phoneAuthService.verifyRegisterOtp(request));
        } catch (Exception ex) {
            return restErrorHandle.handleException(ex, httpRequest, httpResponse);
        }
    }

    @PostMapping(UrlIdentConstant.StorefrontAuth.BASE + "/register/complete")
    @ResponseStatus(HttpStatus.CREATED)
    public BaseRes completeRegistration(@Valid @RequestBody RegisterCompleteRequestDto request,
                                        HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            return restSuccessHandle.handleSuccess(phoneAuthService.completeRegistration(request));
        } catch (Exception ex) {
            return restErrorHandle.handleException(ex, httpRequest, httpResponse);
        }
    }

    @PostMapping(UrlIdentConstant.StorefrontAuth.BASE + "/forgot-password")
    public BaseRes forgotPassword(@Valid @RequestBody SendOtpRequestDto request,
                                  HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            return restSuccessHandle.handleSuccess(phoneAuthService.forgotPassword(request.getPhone()));
        } catch (Exception ex) {
            return restErrorHandle.handleException(ex, httpRequest, httpResponse);
        }
    }

    @PostMapping(UrlIdentConstant.StorefrontAuth.BASE + "/reset-password")
    public BaseRes resetPassword(@Valid @RequestBody ResetPasswordRequestDto request,
                                 HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            return restSuccessHandle.handleSuccess(phoneAuthService.resetPassword(request));
        } catch (Exception ex) {
            return restErrorHandle.handleException(ex, httpRequest, httpResponse);
        }
    }
}
