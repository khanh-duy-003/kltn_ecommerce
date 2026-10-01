package com.pk.core.model.dto.response;

import com.pk.core.model.entity.ProductSkuEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** GET /admin/inventory/stock-levels (mục M spec) - KHÔNG kế thừa BaseDto/UpdateDto (view tính toán
 * từ ProductSkuEntity, không cần lộ audit created/updated ở đây, cùng kiểu OrderItemResponseDto).
 * `warehouseId` luôn trả "MAIN" (hằng InventoryServiceImpl.MAIN_WAREHOUSE_ID) - xem javadoc
 * InventoryService về quyết định chưa hỗ trợ đa kho thật. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockLevelResponseDto {

    private Long skuId;
    private Long productId;
    private String skuCode;
    private String name;
    private String warehouseId;
    private int onHand;
    private int reserved;
    private int available;
    private String stockStatus;

    public static StockLevelResponseDto from(ProductSkuEntity s, String warehouseId) {
        return new StockLevelResponseDto(s.getId(), s.getProductId(), s.getSkuCode(), s.getName(), warehouseId,
                s.getOnHand(), s.getReserved(), s.available(), s.stockStatus());
    }
}
