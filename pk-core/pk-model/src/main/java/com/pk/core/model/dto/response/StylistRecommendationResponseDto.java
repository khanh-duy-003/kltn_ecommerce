package com.pk.core.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Đơn giản hoá so với spec: chỉ trả type=PRODUCT (chưa có domain Set để gợi ý type=SET thật), kèm
 * name/thumbnailUrl để FE hiển thị luôn (spec gốc không có 2 trường này, chỉ có {type,id,reason,
 * matchScore} - thêm cho tiện, không phá cấu trúc gốc). Xem AiStylistServiceImpl về cách suy ra
 * matchScore/reason (heuristic đơn giản, KHÔNG phải mô hình AI/ML thật). */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StylistRecommendationResponseDto {

    private String type;
    private Long id;
    private String name;
    private String thumbnailUrl;
    private String reason;
    private double matchScore;
}
