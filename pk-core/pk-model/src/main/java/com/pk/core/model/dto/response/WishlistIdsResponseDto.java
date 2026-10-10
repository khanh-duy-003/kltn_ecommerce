package com.pk.core.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/** Danh sách id sản phẩm yêu thích (chuỗi). Trả về cả sau khi thêm/xoá để FE đồng bộ trạng thái. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WishlistIdsResponseDto {

    private List<String> productIds;

    /** FE đọc `total` cùng `productIds`. */
    public int getTotal() {
        return productIds == null ? 0 : productIds.size();
    }
}
