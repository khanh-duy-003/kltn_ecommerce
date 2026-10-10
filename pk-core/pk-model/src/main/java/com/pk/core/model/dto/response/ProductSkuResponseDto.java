package com.pk.core.model.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pk.core.common.dto.BaseDto;
import com.pk.core.model.entity.ProductSkuEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductSkuResponseDto extends BaseDto {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;
    private String skuCode;
    private String name;
    private String status;
    private String material;
    private String gemstone;
    private String sizeLabel;
    private String metalColorLabel;
    private BigDecimal caratWeight;
    private BigDecimal weightGram;
    private BigDecimal listPrice;
    private BigDecimal salePrice;
    private int availableStock;
    private String stockStatus;
    private boolean isDefault;
    /** Thuộc tính cấu hình (product_attributes) đã gán cho SKU: [{id, code, name, type, value, values[]}]. */
    private List<Map<String, Object>> attributeBindings = new ArrayList<>();

    public static ProductSkuResponseDto from(ProductSkuEntity s) {
        return BaseDto.of(new ProductSkuResponseDto(s.getId(), s.getSkuCode(), s.getName(), s.getStatus(),
                s.getMaterial(), s.getGemstone(), s.getSizeLabel(), s.getMetalColorLabel(), s.getCaratWeight(),
                s.getWeightGram(), s.getListPrice(), s.getSalePrice(), s.available(), s.stockStatus(),
                s.isDefault(), new ArrayList<>()), s);
    }
}
