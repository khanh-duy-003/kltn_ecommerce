package com.pk.core.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Rút gọn nhiều so với Badge object đầy đủ trong spec (id, name, code, description, type,
 * status, defaultPosition, styleConfig, icon, image, defaultPriorityWeight...) - đây là badge suy ra
 * TỰ ĐỘNG từ dữ liệu tồn kho/ngày publish, KHÔNG đọc từ bảng badge_templates do admin cấu hình
 * (admin loại trừ đợt này) - xem BadgeServiceImpl. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BadgeResponseDto {

    private Long skuId;
    private String badgeType;
    private String displayText;
}
