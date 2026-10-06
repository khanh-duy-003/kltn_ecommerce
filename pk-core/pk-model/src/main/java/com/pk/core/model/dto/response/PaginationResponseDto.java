package com.pk.core.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Phân trang theo spec FE (cart recommendation). nextPage/previousPage: số trang hoặc false nếu không có. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaginationResponseDto {

    private long total;
    private int currentPage;
    private Object nextPage;
    private Object previousPage;
    private boolean hasNextPage;
    private boolean hasPreviousPage;
    private int totalPage;
}
