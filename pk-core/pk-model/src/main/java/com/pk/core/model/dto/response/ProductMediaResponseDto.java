package com.pk.core.model.dto.response;

import com.pk.core.model.entity.ProductMediaEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductMediaResponseDto {

    private String id;
    private String productId;
    private String skuId;
    private String url;
    private String alt;
    private String type;
    private int sortOrder;
    private boolean primary;

    public boolean getIsPrimary() {
        return primary;
    }

    public static ProductMediaResponseDto from(ProductMediaEntity e) {
        return new ProductMediaResponseDto(String.valueOf(e.getId()), String.valueOf(e.getProductId()),
                e.getSkuId() == null ? null : String.valueOf(e.getSkuId()), e.getUrl(), e.getAlt(), e.getMediaType(),
                e.getSortOrder(), e.isPrimary());
    }
}
