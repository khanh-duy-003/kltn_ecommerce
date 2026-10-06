package com.pk.core.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/** Danh sách mẫu nhãn phân trang theo spec FE: {items, total, page, take}. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BadgeTemplatePageResponseDto {

    private List<BadgeTemplateResponseDto> items;
    private long total;
    private int page;
    private int take;
}
