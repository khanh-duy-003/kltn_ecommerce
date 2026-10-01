package com.pk.core.api.rest.admin;

import lombok.RequiredArgsConstructor;

import com.pk.core.business.service.InventoryService;
import com.pk.core.business.web.AbstractRest;
import com.pk.core.common.web.BaseRes;
import com.pk.core.model.constant.admin.UrlAdminConstant;
import com.pk.core.model.dto.request.AdminStockAdjustmentRequestDto;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * API admin - Tồn kho (document/09-tong-hop-api-fe.md mục M). Gói api.rest.admin (pk-api).
 * `warehouseId` (query param GET, field trong body POST) được NHẬN nhưng KHÔNG dùng để lọc/phân biệt
 * kho thật - hệ thống hiện chỉ có 1 kho ngầm định "MAIN" (xem javadoc InventoryService, pk-business,
 * về quyết định thiết kế cuối cùng cho vướng mắc warehouseId đã ghi nhận trước đó).
 */
@RestController
@RequiredArgsConstructor
public class AdminInventoryRest extends AbstractRest {

    private final InventoryService inventoryService;

    @GetMapping(UrlAdminConstant.Inventory.STOCK_LEVELS)
    public BaseRes stockLevels(@RequestParam(required = false) String warehouseId,
                                @RequestParam(required = false) String keyword,
                                HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            return restSuccessHandle.handleSuccess(inventoryService.stockLevels(keyword));
        } catch (Exception ex) {
            return restErrorHandle.handleException(ex, httpRequest, httpResponse);
        }
    }

    @PostMapping(UrlAdminConstant.Inventory.ADJUSTMENTS)
    public BaseRes adjust(@Valid @RequestBody AdminStockAdjustmentRequestDto request,
                           HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            return restSuccessHandle.handleSuccess(inventoryService.adjust(request));
        } catch (Exception ex) {
            return restErrorHandle.handleException(ex, httpRequest, httpResponse);
        }
    }
}
