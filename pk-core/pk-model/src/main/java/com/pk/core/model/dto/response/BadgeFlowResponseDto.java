package com.pk.core.model.dto.response;

import com.pk.core.common.dto.UpdateDto;
import com.pk.core.model.entity.BadgeFlowEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** extends UpdateDto (không phải BaseDto) - khớp BadgeFlowEntity extends UpdateEntity (bảng
 * badge_flow không có deleted_id/deleted_date), gọi tay copyAudit() giống ProductAttributeResponseDto/
 * CmsBlockResponseDto. */
@Getter
@Setter
@NoArgsConstructor
public class BadgeFlowResponseDto extends UpdateDto {

    private Long id;
    private Long badgeId;
    private String ruleType;
    private Long ruleRefId;
    private String channel;
    private int priority;
    private boolean active;

    public static BadgeFlowResponseDto from(BadgeFlowEntity e) {
        BadgeFlowResponseDto dto = new BadgeFlowResponseDto();
        dto.setId(e.getId());
        dto.setBadgeId(e.getBadgeId());
        dto.setRuleType(e.getRuleType());
        dto.setRuleRefId(e.getRuleRefId());
        dto.setChannel(e.getChannel());
        dto.setPriority(e.getPriority());
        dto.setActive(e.isActive());
        dto.copyAudit(e);
        return dto;
    }
}
