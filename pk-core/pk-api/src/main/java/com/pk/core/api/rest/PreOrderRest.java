package com.pk.core.api.rest;

import lombok.RequiredArgsConstructor;

import com.pk.core.business.service.PreOrderService;
import com.pk.core.business.web.AbstractRest;
import com.pk.core.common.web.BaseRes;
import com.pk.core.model.constant.UrlConstant;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** Cấu hình đặt trước đang hiệu lực (công khai). Khi enabled=true, SKU hết hàng vẫn thêm giỏ/báo giá/đặt hàng được
 * (đơn đặt trước) và nhãn storefront của SKU hết hàng là PRE_ORDER. Mở rộng ngoài spec FE. */
@RestController
@RequiredArgsConstructor
public class PreOrderRest extends AbstractRest {

    private final PreOrderService preOrderService;

    @GetMapping(UrlConstant.PreOrder.CURRENT)
    public BaseRes current(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            return restSuccessHandle.handleSuccess(preOrderService.current());
        } catch (Exception ex) {
            return restErrorHandle.handleException(ex, httpRequest, httpResponse);
        }
    }
}
