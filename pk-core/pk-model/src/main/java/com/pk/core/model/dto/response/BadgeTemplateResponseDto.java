package com.pk.core.model.dto.response;

import com.pk.core.common.dto.BaseDto;
import com.pk.core.model.entity.BadgeTemplateEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BadgeTemplateResponseDto extends BaseDto {

    private Long id;
    private String name;
    private String labelText;
    private String color;

    public static BadgeTemplateResponseDto from(BadgeTemplateEntity e) {
        return BaseDto.of(new BadgeTemplateResponseDto(e.getId(), e.getName(), e.getLabelText(), e.getColor()), e);
    }
}
