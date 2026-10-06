package com.pk.core.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/** Danh sách yêu cầu phân trang theo spec FE: {items, total, page, take}. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRequestPageResponseDto {

    private List<CustomerRequestResponseDto> items;
    private long total;
    private int page;
    private int take;
}
