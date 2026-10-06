package com.pk.core.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Đếm luồng theo trạng thái (không phụ thuộc bộ lọc status): {all, active, inactive, draft}. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BadgeFlowCountsDto {

    private long all;
    private long active;
    private long inactive;
    private long draft;
}
