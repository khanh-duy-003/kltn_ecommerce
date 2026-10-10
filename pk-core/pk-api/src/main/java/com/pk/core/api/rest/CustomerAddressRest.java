package com.pk.core.api.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import lombok.RequiredArgsConstructor;

import com.pk.core.business.service.CustomerAddressService;
import com.pk.core.business.web.AbstractRest;
import com.pk.core.common.web.BaseRes;
import com.pk.core.identity.security.model.SecurityUser;
import com.pk.core.model.constant.UrlConstant;
import com.pk.core.model.dto.request.AddressRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Sổ địa chỉ của khách đang đăng nhập (/storefront/me/addresses) - yêu cầu Bearer token (mặc định
 * anyRequest().authenticated() ở SecurityConfig, không cần thêm rule permitAll). */
@RestController
@RequestMapping(UrlConstant.Common.API + UrlConstant.Common.VERSION)
@RequiredArgsConstructor
public class CustomerAddressRest extends AbstractRest {

    private final CustomerAddressService addressService;

    @GetMapping(UrlConstant.Me.ADDRESSES)
    public BaseRes list(@AuthenticationPrincipal SecurityUser principal,
                         HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            return restSuccessHandle.handleSuccess(addressService.listMine(principal.getId()));
        } catch (Exception ex) {
            return restErrorHandle.handleException(ex, httpRequest, httpResponse);
        }
    }

    @PostMapping(UrlConstant.Me.ADDRESSES)
    @ResponseStatus(HttpStatus.CREATED)
    public BaseRes add(@AuthenticationPrincipal SecurityUser principal, @Valid @RequestBody AddressRequestDto request,
                        HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            return restSuccessHandle.handleSuccess(addressService.add(principal.getId(), request));
        } catch (Exception ex) {
            return restErrorHandle.handleException(ex, httpRequest, httpResponse);
        }
    }

    @PutMapping(UrlConstant.Me.ADDRESSES + "/{addressId}")
    public BaseRes update(@AuthenticationPrincipal SecurityUser principal, @PathVariable Long addressId,
                           @Valid @RequestBody AddressRequestDto request,
                           HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            return restSuccessHandle.handleSuccess(addressService.update(principal.getId(), addressId, request));
        } catch (Exception ex) {
            return restErrorHandle.handleException(ex, httpRequest, httpResponse);
        }
    }

    /** 204 No Content: không bọc try/catch (theo mẫu AuthRest.logout) - lỗi (VD 404 không có địa
     * chỉ này) rơi vào GlobalExceptionHandler làm lưới an toàn. */
    @DeleteMapping(UrlConstant.Me.ADDRESSES + "/{addressId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal SecurityUser principal, @PathVariable Long addressId) {
        addressService.delete(principal.getId(), addressId);
    }
}
