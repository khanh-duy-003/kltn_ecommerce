package com.pk.core.business.service;

import com.pk.core.model.dto.request.StylistRequestDto;
import com.pk.core.model.dto.response.SetResponseDto;
import com.pk.core.model.dto.response.StylistRecommendationResponseDto;

import java.util.List;

/** "AI" Stylist/Set Builder - xem AiStylistServiceImpl: đây là HEURISTIC đơn giản dựa trên bộ lọc
 * Catalog sẵn có (occasion/ngân sách), KHÔNG phải mô hình AI/ML thật (dự án chưa tích hợp LLM/ML nào
 * cho việc này). Đặt tên đúng theo spec FE để khớp route, không có nghĩa là "AI" theo đúng nghĩa. */
public interface AiStylistService {

    List<StylistRecommendationResponseDto> recommend(StylistRequestDto req);

    SetResponseDto buildSet(StylistRequestDto req);
}
