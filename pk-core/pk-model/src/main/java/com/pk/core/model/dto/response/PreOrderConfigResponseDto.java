package com.pk.core.model.dto.response;

import com.pk.core.model.entity.PreOrderConfigEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/** Một cấu hình đặt trước (id là chuỗi như các DTO spec FE khác). */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PreOrderConfigResponseDto {

    private String id;
    private boolean enabled;
    private String message;
    private Date createdAt;

    public static PreOrderConfigResponseDto from(PreOrderConfigEntity e) {
        return new PreOrderConfigResponseDto(String.valueOf(e.getId()), e.isEnabled(), e.getMessage(), e.getCreatedDate());
    }
}
