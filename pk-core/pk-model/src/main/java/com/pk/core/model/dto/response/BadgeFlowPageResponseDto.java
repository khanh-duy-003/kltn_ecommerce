package com.pk.core.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/** Danh sách luồng phân trang theo spec FE: {items, total, page, take, counts}. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BadgeFlowPageResponseDto {

    private List<BadgeFlowResponseDto> items;
    private long total;
    private int page;
    private int take;
    private BadgeFlowCountsDto counts;
}
