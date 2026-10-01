package com.pk.core.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Body cho POST/PUT /admin/badge-flow(/{flowId}) (mục P spec). Spec không có path variable cho PUT
 * ("PUT /admin/badge-flow") nhưng badge_flow là bảng NHIỀU dòng (1 badge có thể nhiều luật) nên PUT
 * cần biết sửa dòng nào - Claude THÊM {flowId} vào path PUT (xem RULE-CODE.md mục "Badge admin - bổ
 * khuyết spec thiếu"), cùng tinh thần các PUT .../{id} khác trong dự án. `ruleType`/`channel` là 1
 * trong các hằng BadgeFlowEntity.RULE_CHANNEL_* (validate ở service, không dùng @Pattern để khỏi
 * lặp danh sách giá trị 2 chỗ). */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminBadgeFlowRequestDto {

    @NotNull(message = "badgeId không được để trống")
    private Long badgeId;

    @NotBlank(message = "ruleType không được để trống")
    private String ruleType;

    private Long ruleRefId;

    private String channel;

    private int priority;

    private boolean active = true;
}
