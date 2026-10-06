package com.pk.core.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** 1 mẫu nhãn trong luồng: {badgeTemplateId, priorityWeight, isPinned} (dùng cả cho request lẫn response). */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BadgeFlowTemplateRefDto {

    @NotBlank(message = "badgeTemplateId không được để trống")
    private String badgeTemplateId;

    private Integer priorityWeight;

    private Boolean isPinned;
}
