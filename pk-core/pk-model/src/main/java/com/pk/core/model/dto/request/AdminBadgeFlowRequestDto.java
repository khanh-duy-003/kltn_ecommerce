package com.pk.core.model.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.Map;

/** Body tạo (POST /admin/badge-flow) và thay thế (PUT /admin/badge-flow/{flowId}) luồng hiển thị nhãn theo spec FE. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminBadgeFlowRequestDto {

    @NotBlank(message = "Tên luồng không được để trống")
    @Size(max = 150)
    private String name;

    @Size(max = 500)
    private String description;

    private Date activeFrom;

    private Date activeTo;

    private String status;

    @NotBlank(message = "ruleType không được để trống")
    private String ruleType;

    private Map<String, Object> ruleConfig;

    private String channel;

    @NotEmpty(message = "Phải chọn ít nhất 1 mẫu nhãn")
    @Valid
    private List<BadgeFlowTemplateRefDto> templates;
}
