package com.pk.core.model.dto.response;

import com.pk.core.model.entity.OrderItemEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/** DTO con nằm trong OrderResponseDto.items - KHÔNG kế thừa BaseDto/CreateDto (không cần lộ
 * createdDate của từng dòng hàng ra ngoài, spec FE cũng không có trường này ở `lines[]`). */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponseDto {

    private Long id;
    private Long productId;
    private Long skuId;
    private String name;
    private String imageUrl;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal lineTotal;

    public static OrderItemResponseDto from(OrderItemEntity i) {
        return new OrderItemResponseDto(i.getId(), i.getProductId(), i.getSkuId(), i.getName(), i.getImageUrl(),
                i.getQuantity(), i.getUnitPrice(), i.getLineTotal());
    }
}
