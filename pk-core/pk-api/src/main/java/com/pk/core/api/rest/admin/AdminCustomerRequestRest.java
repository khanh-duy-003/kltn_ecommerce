package com.pk.core.api.rest.admin;

import lombok.RequiredArgsConstructor;

import com.pk.core.business.service.CustomerRequestService;
import com.pk.core.business.web.AbstractRest;
import com.pk.core.common.web.BaseRes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import com.pk.core.model.constant.admin.UrlAdminConstant;
import com.pk.core.model.dto.request.CustomerRequestBulkStatusRequestDto;
import com.pk.core.model.dto.request.CustomerRequestStatusRequestDto;
import com.pk.core.model.dto.response.CustomerRequestResponseDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * API admin - yêu cầu khách hàng (spec FE nhóm Customer Request). Gói api.rest.admin (pk-api). Danh sách tách 3 route theo
 * loại (back-in-stock / order-support / newsletter) cùng bộ lọc; /{id}, /{id}/status, /status/bulk dùng chung.
 */
@RestController
@RequiredArgsConstructor
public class AdminCustomerRequestRest extends AbstractRest {

    private static final String BASE = UrlAdminConstant.CustomerRequest.BASE;

    private final CustomerRequestService customerRequestService;

    @GetMapping(BASE + "/back-in-stock")
    public BaseRes listBackInStock(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "20") int take,
                                   @RequestParam(required = false) String keyword,
                                   @RequestParam(required = false) String status,
                                   @RequestParam(required = false) String timeFilter,
                                   @RequestParam(required = false) String timeFrom,
                                   @RequestParam(required = false) String timeTo,
                                   @RequestParam(required = false) String orderType,
                                   HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        return list("BACK_IN_STOCK", page, take, keyword, status, timeFilter, timeFrom, timeTo, orderType,
                httpRequest, httpResponse);
    }

    @GetMapping(BASE + "/order-support")
    public BaseRes listOrderSupport(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "20") int take,
                                    @RequestParam(required = false) String keyword,
                                    @RequestParam(required = false) String status,
                                    @RequestParam(required = false) String timeFilter,
                                    @RequestParam(required = false) String timeFrom,
                                    @RequestParam(required = false) String timeTo,
                                    @RequestParam(required = false) String orderType,
                                    HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        return list("ORDER_SUPPORT", page, take, keyword, status, timeFilter, timeFrom, timeTo, orderType,
                httpRequest, httpResponse);
    }

    @GetMapping(BASE + "/newsletter")
    public BaseRes listNewsletter(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "20") int take,
                                  @RequestParam(required = false) String keyword,
                                  @RequestParam(required = false) String status,
                                  @RequestParam(required = false) String timeFilter,
                                  @RequestParam(required = false) String timeFrom,
                                  @RequestParam(required = false) String timeTo,
                                  @RequestParam(required = false) String orderType,
                                  HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        return list("NEWSLETTER", page, take, keyword, status, timeFilter, timeFrom, timeTo, orderType,
                httpRequest, httpResponse);
    }

    @GetMapping(BASE + "/{id}")
    public BaseRes detail(@PathVariable Long id, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            CustomerRequestResponseDto dto = customerRequestService.findById(id);
            return restSuccessHandle.handleSuccess(dto);
        } catch (Exception ex) {
            return restErrorHandle.handleException(ex, httpRequest, httpResponse);
        }
    }

    @PatchMapping(BASE + "/{id}/status")
    public BaseRes updateStatus(@PathVariable Long id, @Valid @RequestBody CustomerRequestStatusRequestDto request,
                                HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            return restSuccessHandle.handleSuccess(customerRequestService.updateStatus(id, request.getStatus()));
        } catch (Exception ex) {
            return restErrorHandle.handleException(ex, httpRequest, httpResponse);
        }
    }

    @PatchMapping(BASE + "/status/bulk")
    public BaseRes bulkUpdateStatus(@Valid @RequestBody CustomerRequestBulkStatusRequestDto request,
                                    HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            return restSuccessHandle.handleSuccess(customerRequestService.bulkUpdateStatus(request));
        } catch (Exception ex) {
            return restErrorHandle.handleException(ex, httpRequest, httpResponse);
        }
    }

    private BaseRes list(String type, int page, int take, String keyword, String status, String timeFilter,
                         String timeFrom, String timeTo, String orderType,
                         HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            return restSuccessHandle.handleSuccess(customerRequestService.list(type, page, take, keyword, status,
                    timeFilter, timeFrom, timeTo, orderType));
        } catch (Exception ex) {
            return restErrorHandle.handleException(ex, httpRequest, httpResponse);
        }
    }
}
