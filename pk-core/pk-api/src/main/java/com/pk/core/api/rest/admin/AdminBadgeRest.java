package com.pk.core.api.rest.admin;

import lombok.RequiredArgsConstructor;

import com.pk.core.business.service.BadgeService;
import com.pk.core.business.web.AbstractRest;
import com.pk.core.common.web.BaseRes;
import com.pk.core.model.constant.admin.UrlAdminConstant;
import com.pk.core.model.dto.request.AdminBadgeFlowRequestDto;
import com.pk.core.model.dto.request.AdminBadgeTemplateRequestDto;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * API admin - Badge/nhãn dán sản phẩm (document/09-tong-hop-api-fe.md mục P). Gói api.rest.admin
 * (pk-api). Spec chỉ liệt kê GET/PATCH/DELETE theo id cho badge-templates (không có list/create rõ
 * ràng) và GET/POST/PUT không path variable cho badge-flow - Claude bổ khuyết list/create cho
 * templates và thêm {flowId} cho PUT flow (xem javadoc AdminBadgeTemplateRequestDto/
 * AdminBadgeFlowRequestDto + RULE-CODE.md).
 */
@RestController
@RequiredArgsConstructor
public class AdminBadgeRest extends AbstractRest {

    private final BadgeService badgeService;

    // ---------- Badge templates ----------

    @GetMapping(UrlAdminConstant.Badge.TEMPLATES)
    public BaseRes listTemplates(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            return restSuccessHandle.handleSuccess(badgeService.findAllTemplates());
        } catch (Exception ex) {
            return restErrorHandle.handleException(ex, httpRequest, httpResponse);
        }
    }

    @PostMapping(UrlAdminConstant.Badge.TEMPLATES)
    @ResponseStatus(HttpStatus.CREATED)
    public BaseRes createTemplate(@Valid @RequestBody AdminBadgeTemplateRequestDto request,
                                   HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            return restSuccessHandle.handleSuccess(badgeService.createTemplate(request));
        } catch (Exception ex) {
            return restErrorHandle.handleException(ex, httpRequest, httpResponse);
        }
    }

    @GetMapping(UrlAdminConstant.Badge.TEMPLATES + "/{badgeId}")
    public BaseRes templateDetail(@PathVariable Long badgeId,
                                   HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            return restSuccessHandle.handleSuccess(badgeService.findTemplateById(badgeId));
        } catch (Exception ex) {
            return restErrorHandle.handleException(ex, httpRequest, httpResponse);
        }
    }

    @PatchMapping(UrlAdminConstant.Badge.TEMPLATES + "/{badgeId}")
    public BaseRes updateTemplate(@PathVariable Long badgeId, @Valid @RequestBody AdminBadgeTemplateRequestDto request,
                                   HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            return restSuccessHandle.handleSuccess(badgeService.updateTemplate(badgeId, request));
        } catch (Exception ex) {
            return restErrorHandle.handleException(ex, httpRequest, httpResponse);
        }
    }

    @DeleteMapping(UrlAdminConstant.Badge.TEMPLATES + "/{badgeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTemplate(@PathVariable Long badgeId) {
        badgeService.deleteTemplate(badgeId);
    }

    // ---------- Badge flow ----------

    @GetMapping(UrlAdminConstant.Badge.FLOW)
    public BaseRes listFlows(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            return restSuccessHandle.handleSuccess(badgeService.findAllFlows());
        } catch (Exception ex) {
            return restErrorHandle.handleException(ex, httpRequest, httpResponse);
        }
    }

    @PostMapping(UrlAdminConstant.Badge.FLOW)
    @ResponseStatus(HttpStatus.CREATED)
    public BaseRes createFlow(@Valid @RequestBody AdminBadgeFlowRequestDto request,
                               HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            return restSuccessHandle.handleSuccess(badgeService.createFlow(request));
        } catch (Exception ex) {
            return restErrorHandle.handleException(ex, httpRequest, httpResponse);
        }
    }

    @PutMapping(UrlAdminConstant.Badge.FLOW + "/{flowId}")
    public BaseRes updateFlow(@PathVariable Long flowId, @Valid @RequestBody AdminBadgeFlowRequestDto request,
                               HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            return restSuccessHandle.handleSuccess(badgeService.updateFlow(flowId, request));
        } catch (Exception ex) {
            return restErrorHandle.handleException(ex, httpRequest, httpResponse);
        }
    }
}
