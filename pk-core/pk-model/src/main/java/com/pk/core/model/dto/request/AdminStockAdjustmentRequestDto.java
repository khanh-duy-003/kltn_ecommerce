package com.pk.core.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Body cho POST /admin/inventory/adjustments (mục M spec). `warehouseId` KHÔNG bắt buộc và bị bỏ
 * qua (decorative) - hệ thống hiện chỉ có 1 kho ngầm định "MAIN", CHƯA hỗ trợ đa kho thật (xem
 * javadoc InventoryService + RULE-CODE.md mục Inventory). `reason` phải là 1 trong RECEIPT/DAMAGE/
 * CORRECTION (validate ở service, 400 VALIDATION_FAILED nếu sai). `quantityDelta` có thể âm (giảm)
 * hoặc dương (tăng) - âm khiến on_hand < 0 thì 422 NEGATIVE_STOCK. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminStockAdjustmentRequestDto {

    @NotNull(message = "skuId không được để trống")
    private Long skuId;

    @NotNull(message = "quantityDelta không được để trống")
    private Integer quantityDelta;

    @NotBlank(message = "reason không được để trống")
    private String reason;

    private String warehouseId;
}
