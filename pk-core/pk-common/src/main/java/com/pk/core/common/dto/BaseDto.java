package com.pk.core.common.dto;

import com.pk.core.common.entity.BaseEntity;

/**
 * DTO đầy đủ audit (tương ứng BaseEntity). Chuỗi: BaseDto -> DeleteDto -> UpdateDto -> CreateDto.
 * Dùng cho DTO phản hồi được dựng từ entity: BaseDto.of(new XxxDto(...), entity).
 */
public abstract class BaseDto extends DeleteDto {

    public static <T extends BaseDto> T of(T dto, BaseEntity entity) {
        dto.copyAudit(entity);
        return dto;
    }
}
