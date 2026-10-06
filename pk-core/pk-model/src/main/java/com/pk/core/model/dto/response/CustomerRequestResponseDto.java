package com.pk.core.model.dto.response;

import com.pk.core.model.entity.CustomerRequestEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/** Một yêu cầu khách hàng theo spec FE (mọi id là chuỗi; requestedAt = createdAt = lúc khách gửi). */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRequestResponseDto {

    private String id;
    private String type;
    private String contactChannel;
    private String contactValue;
    private Date requestedAt;
    private Date createdAt;
    private String status;
    private String productId;
    /** Id SKU (spec gọi là productVariantId). */
    private String productVariantId;
    private String skuCode;
    private String productNameSnapshot;
    private String productImageUrlSnapshot;
    private String variantTextSnapshot;
    private String orderId;
    private String orderCode;
    private String orderType;

    public static CustomerRequestResponseDto from(CustomerRequestEntity e) {
        return new CustomerRequestResponseDto(String.valueOf(e.getId()), e.getType(), e.getContactChannel(),
                e.getContactValue(), e.getCreatedDate(), e.getCreatedDate(), e.getStatus(),
                idOf(e.getProductId()), idOf(e.getSkuId()), e.getSkuCode(), e.getProductNameSnapshot(),
                e.getProductImageUrlSnapshot(), e.getVariantTextSnapshot(), idOf(e.getOrderId()),
                e.getOrderCode(), e.getOrderType());
    }

    private static String idOf(Long id) {
        return id == null ? null : String.valueOf(id);
    }
}
