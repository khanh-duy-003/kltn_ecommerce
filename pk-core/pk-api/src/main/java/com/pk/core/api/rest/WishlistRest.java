package com.pk.core.api.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import lombok.RequiredArgsConstructor;

import com.pk.core.business.service.WishlistService;
import com.pk.core.business.web.AbstractRest;
import com.pk.core.common.web.BaseRes;
import com.pk.core.identity.security.model.SecurityUser;
import com.pk.core.model.constant.UrlConstant;
import com.pk.core.model.dto.request.WishlistRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** Yêu thích (/storefront/product/customer/wishlist, theo spec FE) - bắt buộc đăng nhập (SecurityConfig). */
@RestController
@RequestMapping(UrlConstant.Common.API + UrlConstant.Common.VERSION)
@RequiredArgsConstructor
public class WishlistRest extends AbstractRest {

    private final WishlistService wishlistService;

    @GetMapping(UrlConstant.Wishlist.BASE)
    public BaseRes list(@AuthenticationPrincipal SecurityUser principal,
                        HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            java.util.List<?> items = wishlistService.list(principal.getId());
            // FE đọc { total, list } (ICustomerWishlistResponse).
            java.util.Map<String, Object> body = new java.util.LinkedHashMap<>();
            body.put("total", items.size());
            body.put("list", items);
            return restSuccessHandle.handleSuccess(body);
        } catch (Exception ex) {
            return restErrorHandle.handleException(ex, httpRequest, httpResponse);
        }
    }

    @PostMapping(UrlConstant.Wishlist.BASE)
    public BaseRes add(@AuthenticationPrincipal SecurityUser principal, @Valid @RequestBody WishlistRequestDto request,
                       HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            return restSuccessHandle.handleSuccess(wishlistService.add(principal.getId(), request.getProductIds()));
        } catch (Exception ex) {
            return restErrorHandle.handleException(ex, httpRequest, httpResponse);
        }
    }

    @DeleteMapping(UrlConstant.Wishlist.BASE)
    public BaseRes remove(@AuthenticationPrincipal SecurityUser principal,
                          @Valid @RequestBody WishlistRequestDto request,
                          HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            return restSuccessHandle.handleSuccess(wishlistService.remove(principal.getId(), request.getProductIds()));
        } catch (Exception ex) {
            return restErrorHandle.handleException(ex, httpRequest, httpResponse);
        }
    }

    @GetMapping(UrlConstant.Wishlist.BASE + "/product-ids")
    public BaseRes productIds(@AuthenticationPrincipal SecurityUser principal,
                              HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            return restSuccessHandle.handleSuccess(wishlistService.ids(principal.getId()));
        } catch (Exception ex) {
            return restErrorHandle.handleException(ex, httpRequest, httpResponse);
        }
    }

    @GetMapping(UrlConstant.Wishlist.BASE + "/{productId}")
    public BaseRes status(@AuthenticationPrincipal SecurityUser principal, @PathVariable String productId,
                          HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            return restSuccessHandle.handleSuccess(wishlistService.status(principal.getId(), productId));
        } catch (Exception ex) {
            return restErrorHandle.handleException(ex, httpRequest, httpResponse);
        }
    }
}
