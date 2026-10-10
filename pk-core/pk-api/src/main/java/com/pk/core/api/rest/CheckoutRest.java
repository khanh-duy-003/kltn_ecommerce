package com.pk.core.api.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import lombok.RequiredArgsConstructor;

import com.pk.core.business.service.CheckoutService;
import com.pk.core.business.web.AbstractRest;
import com.pk.core.common.web.BaseRes;
import com.pk.core.identity.security.model.SecurityUser;
import com.pk.core.model.constant.UrlConstant;
import com.pk.core.model.dto.request.QuoteRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** Xem trước giá đơn hàng (POST /storefront/checkout/quote). Spec FE ghi "Không cần đăng nhập" (dựa
 * cart context) nhưng thiết kế ở đây tham chiếu addressId đã lưu của khách (giống OrderRest.create,
 * chưa hỗ trợ khách vãng lai gửi địa chỉ rời rạc trong request) nên CỐ Ý bắt buộc đăng nhập - route
 * này KHÔNG có trong danh sách permitAll của SecurityConfig, rơi vào anyRequest().authenticated()
 * mặc định. Đây là khác biệt có chủ đích so với spec, đã ghi rõ trong SecurityConfig + changelog. */
@RestController
@RequestMapping(UrlConstant.Common.API + UrlConstant.Common.VERSION)
@RequiredArgsConstructor
public class CheckoutRest extends AbstractRest {

    private final CheckoutService checkoutService;

    @PostMapping(UrlConstant.Checkout.QUOTE)
    public BaseRes quote(@AuthenticationPrincipal SecurityUser principal, @Valid @RequestBody QuoteRequestDto request,
                          HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            return restSuccessHandle.handleSuccess(checkoutService.quote(principal.getId(), request));
        } catch (Exception ex) {
            return restErrorHandle.handleException(ex, httpRequest, httpResponse);
        }
    }
}
