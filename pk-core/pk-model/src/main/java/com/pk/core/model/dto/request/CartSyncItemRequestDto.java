package com.pk.core.model.dto.request;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Một dòng của {@link CartSyncRequestDto}. {@code variationId} = id SKU (FE gọi SKU là "variation").
 * Các field setId/selectedPackagingRelationIds có trong spec FE: setId hiện CHƯA hỗ trợ (dòng set bị từ
 * chối 400 CART_ITEM_INVALID); selectedPackagingRelationIds và các field lạ khác (setComponents, utm_data)
 * được nhận nhưng bỏ qua.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartSyncItemRequestDto {

    private String variationId;

    private String setId;

    @Min(1)
    private int quantity;

    private List<String> selectedPackagingRelationIds;
}
