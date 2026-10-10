package com.pk.core.api.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import lombok.RequiredArgsConstructor;

import com.pk.core.business.service.AiStylistService;
import com.pk.core.business.web.AbstractRest;
import com.pk.core.common.web.BaseRes;
import com.pk.core.model.constant.UrlConstant;
import com.pk.core.model.dto.request.StylistRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** Gợi ý sản phẩm/set trang sức - KHÔNG cần đăng nhập (permitAll ở SecurityConfig). Xem cảnh báo ở
 * AiStylistService javadoc: heuristic đơn giản, KHÔNG phải AI/ML thật. */
@RestController
@RequestMapping(UrlConstant.Common.API + UrlConstant.Common.VERSION)
@RequiredArgsConstructor
public class AiRest extends AbstractRest {

    private final AiStylistService aiStylistService;

    @PostMapping(UrlConstant.Ai.STYLIST_RECOMMENDATIONS)
    public BaseRes recommend(@Valid @RequestBody StylistRequestDto request,
                              HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            return restSuccessHandle.handleSuccess(aiStylistService.recommend(request));
        } catch (Exception ex) {
            return restErrorHandle.handleException(ex, httpRequest, httpResponse);
        }
    }

    @PostMapping(UrlConstant.Ai.SET_BUILDER)
    public BaseRes setBuilder(@Valid @RequestBody StylistRequestDto request,
                               HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            return restSuccessHandle.handleSuccess(aiStylistService.buildSet(request));
        } catch (Exception ex) {
            return restErrorHandle.handleException(ex, httpRequest, httpResponse);
        }
    }
}
