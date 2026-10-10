package com.pk.core.api.rest.admin;

import org.springframework.web.bind.annotation.RequestMapping;
import com.pk.core.model.constant.UrlConstant;
import lombok.RequiredArgsConstructor;

import com.pk.core.business.service.PromotionService;
import com.pk.core.business.service.VoucherService;
import com.pk.core.business.web.AbstractRest;
import com.pk.core.common.web.BaseRes;
import com.pk.core.model.constant.admin.UrlAdminConstant;
import com.pk.core.model.dto.request.AdminPromotionRequestDto;
import com.pk.core.model.dto.request.AdminVoucherRequestDto;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** API admin - Khuyến mãi & Voucher (document/09-tong-hop-api-fe.md mục K). Gói api.rest.admin
 * (pk-api). */
@RestController
@RequestMapping(UrlConstant.Common.API + UrlConstant.Common.VERSION)
@RequiredArgsConstructor
public class AdminPromotionVoucherRest extends AbstractRest {

    private final PromotionService promotionService;
    private final VoucherService voucherService;

    // ---------- Promotions ----------

    @GetMapping(UrlAdminConstant.Promotion.BASE)
    public BaseRes listPromotions(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            return restSuccessHandle.handleSuccess(promotionService.findAll());
        } catch (Exception ex) {
            return restErrorHandle.handleException(ex, httpRequest, httpResponse);
        }
    }

    @PostMapping(UrlAdminConstant.Promotion.BASE)
    @ResponseStatus(HttpStatus.CREATED)
    public BaseRes createPromotion(@Valid @RequestBody AdminPromotionRequestDto request,
                                    HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            return restSuccessHandle.handleSuccess(promotionService.create(request));
        } catch (Exception ex) {
            return restErrorHandle.handleException(ex, httpRequest, httpResponse);
        }
    }

    @GetMapping(UrlAdminConstant.Promotion.BASE + "/{promotionId}")
    public BaseRes promotionDetail(@PathVariable Long promotionId,
                                    HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            return restSuccessHandle.handleSuccess(promotionService.findById(promotionId));
        } catch (Exception ex) {
            return restErrorHandle.handleException(ex, httpRequest, httpResponse);
        }
    }

    @PutMapping(UrlAdminConstant.Promotion.BASE + "/{promotionId}")
    public BaseRes updatePromotion(@PathVariable Long promotionId,
                                    @Valid @RequestBody AdminPromotionRequestDto request,
                                    HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            return restSuccessHandle.handleSuccess(promotionService.update(promotionId, request));
        } catch (Exception ex) {
            return restErrorHandle.handleException(ex, httpRequest, httpResponse);
        }
    }

    @DeleteMapping(UrlAdminConstant.Promotion.BASE + "/{promotionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePromotion(@PathVariable Long promotionId) {
        promotionService.delete(promotionId);
    }

    // ---------- Vouchers ----------

    @GetMapping(UrlAdminConstant.Voucher.BASE)
    public BaseRes listVouchers(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            return restSuccessHandle.handleSuccess(voucherService.findAllForAdmin());
        } catch (Exception ex) {
            return restErrorHandle.handleException(ex, httpRequest, httpResponse);
        }
    }

    @PostMapping(UrlAdminConstant.Voucher.BASE)
    @ResponseStatus(HttpStatus.CREATED)
    public BaseRes createVoucher(@Valid @RequestBody AdminVoucherRequestDto request,
                                  HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            return restSuccessHandle.handleSuccess(voucherService.create(request));
        } catch (Exception ex) {
            return restErrorHandle.handleException(ex, httpRequest, httpResponse);
        }
    }

    @PutMapping(UrlAdminConstant.Voucher.BASE + "/{voucherId}")
    public BaseRes updateVoucher(@PathVariable Long voucherId,
                                  @Valid @RequestBody AdminVoucherRequestDto request,
                                  HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            return restSuccessHandle.handleSuccess(voucherService.update(voucherId, request));
        } catch (Exception ex) {
            return restErrorHandle.handleException(ex, httpRequest, httpResponse);
        }
    }

    @DeleteMapping(UrlAdminConstant.Voucher.BASE + "/{voucherId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteVoucher(@PathVariable Long voucherId) {
        voucherService.delete(voucherId);
    }
}
