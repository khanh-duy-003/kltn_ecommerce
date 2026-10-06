package com.pk.core.api.rest;

import lombok.RequiredArgsConstructor;

import com.pk.core.business.service.CustomerRequestService;
import com.pk.core.business.web.AbstractRest;
import com.pk.core.common.web.BaseRes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import com.pk.core.model.constant.UrlConstant;
import com.pk.core.model.dto.request.BackInStockRequestDto;
import com.pk.core.model.dto.request.NewsletterRequestDto;
import com.pk.core.model.dto.request.OrderSupportRequestDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** Khách gửi yêu cầu (/storefront/customer-request/...) - công khai, chỉ POST (xem SecurityConfig). Trả 200 + {success, message} theo spec. */
@RestController
@RequiredArgsConstructor
public class CustomerRequestRest extends AbstractRest {

    private final CustomerRequestService customerRequestService;

    @PostMapping(UrlConstant.CustomerRequest.BASE + "/back-in-stock")
    public BaseRes backInStock(@Valid @RequestBody BackInStockRequestDto request,
                               HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            return restSuccessHandle.handleSuccess(customerRequestService.createBackInStock(request));
        } catch (Exception ex) {
            return restErrorHandle.handleException(ex, httpRequest, httpResponse);
        }
    }

    @PostMapping(UrlConstant.CustomerRequest.BASE + "/order-support")
    public BaseRes orderSupport(@Valid @RequestBody OrderSupportRequestDto request,
                                HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            return restSuccessHandle.handleSuccess(customerRequestService.createOrderSupport(request));
        } catch (Exception ex) {
            return restErrorHandle.handleException(ex, httpRequest, httpResponse);
        }
    }

    @PostMapping(UrlConstant.CustomerRequest.BASE + "/newsletter")
    public BaseRes newsletter(@Valid @RequestBody NewsletterRequestDto request,
                              HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            return restSuccessHandle.handleSuccess(customerRequestService.subscribeNewsletter(request));
        } catch (Exception ex) {
            return restErrorHandle.handleException(ex, httpRequest, httpResponse);
        }
    }
}
