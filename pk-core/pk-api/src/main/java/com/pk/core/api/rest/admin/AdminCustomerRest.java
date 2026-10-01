package com.pk.core.api.rest.admin;

import lombok.RequiredArgsConstructor;

import com.pk.core.business.service.CustomerService;
import com.pk.core.business.web.AbstractRest;
import com.pk.core.common.web.BaseRes;
import com.pk.core.model.constant.admin.UrlAdminConstant;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * API admin - Khách hàng (document/09-tong-hop-api-fe.md mục N). Gói api.rest.admin (pk-api). CHỈ
 * đọc (GET) - spec không có API tạo/sửa/xoá khách hàng từ phía admin (tài khoản khách tự đăng ký
 * qua /storefront/auth).
 */
@RestController
@RequiredArgsConstructor
public class AdminCustomerRest extends AbstractRest {

    private final CustomerService customerService;

    @GetMapping(UrlAdminConstant.Customer.BASE)
    public BaseRes list(@RequestParam(required = false) String keyword,
                         @RequestParam(required = false, defaultValue = "1") int page,
                         @RequestParam(required = false, defaultValue = "20") int take,
                         HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            return restSuccessHandle.handleSuccess(customerService.search(keyword, page, take));
        } catch (Exception ex) {
            return restErrorHandle.handleException(ex, httpRequest, httpResponse);
        }
    }

    @GetMapping(UrlAdminConstant.Customer.BASE + "/{customerId}")
    public BaseRes detail(@PathVariable Long customerId,
                           HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            return restSuccessHandle.handleSuccess(customerService.findByIdForAdmin(customerId));
        } catch (Exception ex) {
            return restErrorHandle.handleException(ex, httpRequest, httpResponse);
        }
    }
}
