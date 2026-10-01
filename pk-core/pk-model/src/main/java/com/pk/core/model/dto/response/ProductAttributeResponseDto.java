package com.pk.core.model.dto.response;

import com.pk.core.common.dto.UpdateDto;
import com.pk.core.model.entity.ProductAttributeEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/** extends UpdateDto (không phải BaseDto) - khớp đúng mức kế thừa của {@link ProductAttributeEntity}
 * (extends UpdateEntity, bảng product_attributes không có cột deleted_id/deleted_date). Không có
 * static `of()` sẵn cho mức UpdateDto (chỉ BaseDto có) nên gọi tay `copyAudit()`, giống
 * OrderResponseDto. */
@Getter
@Setter
@NoArgsConstructor
public class ProductAttributeResponseDto extends UpdateDto {

    private Long id;
    private String name;
    private String code;
    private String type;
    private List<String> options;

    public static ProductAttributeResponseDto from(ProductAttributeEntity e) {
        ProductAttributeResponseDto dto = new ProductAttributeResponseDto();
        dto.setId(e.getId());
        dto.setName(e.getName());
        dto.setCode(e.getCode());
        dto.setType(e.getType());
        dto.setOptions(e.getOptionsList());
        dto.copyAudit(e);
        return dto;
    }
}
