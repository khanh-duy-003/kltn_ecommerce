package com.pk.core.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/** Một sản phẩm trong danh sách yêu thích (spec FE chưa định nghĩa schema response, đây là bản tối thiểu đủ để hiển thị). */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WishlistItemResponseDto {

    private String productId;
    private String name;
    private String slug;
    private String thumbnailUrl;
    /** Giá cơ sở của sản phẩm (products.base_price), số nguyên VND. */
    private Long basePrice;
    /** Thời điểm thêm vào yêu thích. */
    private Date addedAt;
}
