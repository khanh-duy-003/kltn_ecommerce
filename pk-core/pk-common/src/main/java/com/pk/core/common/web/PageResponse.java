package com.pk.core.common.web;

import java.util.List;
import java.util.function.Function;

/** Trang kết quả gọn cho FE; không lộ cấu trúc nội bộ của Spring Data.
 * Quy ước phân trang CHUNG cho cả dự án (khớp document/09-tong-hop-api-fe.md "Quy ước chung"):
 * `page` 1-based (mặc định 1), `take`/size mặc định 20, tối đa 100. */
public record PageResponse<T>(List<T> content, int page, int size, long totalElements, int totalPages) {

    private static final int DEFAULT_TAKE = 20;
    private static final int MAX_TAKE = 100;

    /** Cắt trang một danh sách đã có sẵn trong bộ nhớ (page 1-based). Dùng khi tầng service đã tự
     * lọc/sắp xếp bằng Java thay vì LIMIT/OFFSET ở SQL (xem ProductServiceImpl, OrderServiceImpl). */
    public static <T> PageResponse<T> paginate(List<T> all, int page, int take) {
        int p = page > 0 ? page : 1;
        int t = take > 0 ? Math.min(take, MAX_TAKE) : DEFAULT_TAKE;
        int total = all.size();
        int totalPages = (int) Math.ceil(total / (double) t);
        int from = Math.min((p - 1) * t, total);
        int to = Math.min(from + t, total);
        return new PageResponse<>(all.subList(from, to), p, t, total, totalPages);
    }

    public <R> PageResponse<R> map(Function<T, R> mapper) {
        return new PageResponse<>(content.stream().map(mapper).toList(), page, size, totalElements, totalPages);
    }
}
