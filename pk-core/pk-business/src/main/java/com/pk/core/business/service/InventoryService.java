package com.pk.core.business.service;

import com.pk.core.model.dto.request.AdminStockAdjustmentRequestDto;
import com.pk.core.model.dto.response.StockLevelResponseDto;

import java.util.List;

/** Admin mục M spec (document/09-tong-hop-api-fe.md) - "Tồn kho". Quyết định thiết kế (chốt trong
 * quá trình làm, xem RULE-CODE.md mục Inventory admin): KHÔNG tạo bảng `warehouses` mới - tái dùng
 * thẳng `product_skus.on_hand`/`reserved` có sẵn (V1__init.sql), coi hệ thống có ĐÚNG 1 kho ngầm định
 * duy nhất (`warehouseId` = hằng "MAIN", chỉ mang tính trang trí/tương thích FE, KHÔNG lọc/phân biệt
 * kho thật). Giải quyết gọn mâu thuẫn "spec có warehouseId nhưng DB không multi-warehouse" đã ghi
 * nhận trước đó mà không cần đổi schema. */
public interface InventoryService {

    /** GET /admin/inventory/stock-levels. `keyword` lọc theo skuCode/tên (tương tự ProductSkuRepo.
     * searchByKeyword) - spec chỉ nêu filter `warehouseId` nhưng filter đó decorative (xem trên) nên
     * KHÔNG cần tham số warehouseId ở đây; Rest vẫn nhận query param warehouseId cho khớp spec nhưng
     * bỏ qua giá trị, không truyền xuống tầng service. */
    List<StockLevelResponseDto> stockLevels(String keyword);

    /** POST /admin/inventory/adjustments - 422 NEGATIVE_STOCK nếu on_hand sau điều chỉnh sẽ âm. */
    StockLevelResponseDto adjust(AdminStockAdjustmentRequestDto req);
}
