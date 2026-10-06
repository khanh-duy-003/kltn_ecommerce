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

/** Nhãn dán theo danh sách SKU - KHÔNG cần đăng nhập (permitAll ở SecurityConfig). Đọc cấu hình badge-template/
 * badge-flow do admin tạo, quy tắc PRE_ORDER -> OUT_OF_STOCK -> CAMPAIGN, mỗi SKU tối đa 1 nhãn (xem BadgeService).
 * `channel` (mặc định WEB) là tham số mở rộng ngoài spec để lọc flow theo kênh. */
@RestController
@RequiredArgsConstructor
public class BadgeRest extends AbstractRest {

    private final BadgeService badgeService;

    @GetMapping(UrlConstant.Badge.BASE)
    public BaseRes forSkus(@RequestParam(required = false) List<String> skuIds,
                            @RequestParam(required = false) String channel,
                            HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            return restSuccessHandle.handleSuccess(badgeService.forSkus(skuIds, channel));
        } catch (Exception ex) {
            return restErrorHandle.handleException(ex, httpRequest, httpResponse);
        }
    }
}
