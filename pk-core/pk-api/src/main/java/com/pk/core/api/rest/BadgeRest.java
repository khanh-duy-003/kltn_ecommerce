package com.pk.core.api.rest;

import lombok.RequiredArgsConstructor;

import com.pk.core.business.service.BadgeService;
import com.pk.core.business.web.AbstractRest;
import com.pk.core.common.web.BaseRes;
import com.pk.core.model.constant.UrlConstant;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Nhãn dán sản phẩm theo danh sách SKU - KHÔNG cần đăng nhập (permitAll ở SecurityConfig). Xem cảnh
 * báo ở BadgeService javadoc: suy ra tự động từ tồn kho/ngày publish, KHÔNG phải hệ thống admin cấu
 * hình được như spec. */
@RestController
@RequiredArgsConstructor
public class BadgeRest extends AbstractRest {

    private final BadgeService badgeService;

    @GetMapping(UrlConstant.Badge.BASE)
    public BaseRes forSkus(@RequestParam List<Long> skuIds,
                            HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            return restSuccessHandle.handleSuccess(badgeService.forSkus(skuIds));
        } catch (Exception ex) {
            return restErrorHandle.handleException(ex, httpRequest, httpResponse);
        }
    }
}
