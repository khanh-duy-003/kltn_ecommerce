package com.pk.core.api.rest;

import lombok.RequiredArgsConstructor;

import com.pk.core.business.service.PaymentService;
import com.pk.core.business.web.AbstractRest;
import com.pk.core.common.web.BaseRes;
import com.pk.core.model.constant.UrlConstant;
import com.pk.core.model.dto.request.PaymentCallbackRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** Webhook callback từ cổng thanh toán - KHÔNG có Bearer token (permitAll ở SecurityConfig), tự xác
 * thực bằng chữ ký trong PaymentServiceImpl (xem cảnh báo giới hạn ở đó: chữ ký tự quy ước, chưa phải
 * thuật toán VNPay/MoMo thật). */
@RestController
@RequiredArgsConstructor
public class PaymentRest extends AbstractRest {

    private final PaymentService paymentService;

    @PostMapping(UrlConstant.Payment.BASE + "/{paymentId}/callback")
    public BaseRes callback(@PathVariable Long paymentId, @Valid @RequestBody PaymentCallbackRequestDto request,
                             HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            paymentService.handleCallback(paymentId, request);
            return restSuccessHandle.handleSuccess("OK");
        } catch (Exception ex) {
            return restErrorHandle.handleException(ex, httpRequest, httpResponse);
        }
    }
}
