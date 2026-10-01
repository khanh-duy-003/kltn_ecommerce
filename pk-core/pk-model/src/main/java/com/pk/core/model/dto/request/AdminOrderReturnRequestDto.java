package com.pk.core.model.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/** Body cho POST /admin/orders/{orderCode}/return (mục L spec). `lineIds` là id các OrderItemEntity
 * (KHÔNG phải skuId) được xác nhận hoàn trả - chỉ áp dụng khi đơn đang DELIVERED (xem
 * OrderEntity.isReturnable()). */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminOrderReturnRequestDto {

    @NotEmpty(message = "Danh sách dòng hàng hoàn trả không được để trống")
    private List<Long> lineIds;
}
