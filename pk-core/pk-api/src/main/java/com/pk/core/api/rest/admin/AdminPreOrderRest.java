package com.pk.core.api.rest.admin;

import lombok.RequiredArgsConstructor;

import com.pk.core.business.service.PreOrderService;
import com.pk.core.business.web.AbstractRest;
import com.pk.core.common.web.BaseRes;
import com.pk.core.identity.security.model.SecurityUser;
import com.pk.core.model.constant.admin.UrlAdminConstant;
import com.pk.core.model.dto.request.PreOrderConfigRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** API admin - cấu hình đặt trước (spec FE nhóm Pre-order). POST trả 200 + cấu hình vừa tạo (theo spec, không phải 201). */
@RestController
@RequiredArgsConstructor
public class AdminPreOrderRest extends AbstractRest {

    private final PreOrderService preOrderService;

    @GetMapping(UrlAdminConstant.PreOrder.BASE)
    public BaseRes list(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            return restSuccessHandle.handleSuccess(preOrderService.list());
        } catch (Exception ex) {
            return restErrorHandle.handleException(ex, httpRequest, httpResponse);
        }
    }

    @PostMapping(UrlAdminConstant.PreOrder.BASE)
    public BaseRes create(@AuthenticationPrincipal SecurityUser principal,
                          @Valid @RequestBody PreOrderConfigRequestDto request,
                          HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            return restSuccessHandle.handleSuccess(preOrderService.create(principal.getId(), request));
        } catch (Exception ex) {
            return restErrorHandle.handleException(ex, httpRequest, httpResponse);
        }
    }
}
