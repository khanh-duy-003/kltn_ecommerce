package com.pk.core.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Phần "product" lồng trong một dòng giỏ hàng (spec FE). */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartProductResponseDto {

    private String id;
    private String name;
    private String slug;
    /** Ảnh đại diện (products.thumbnail_url). */
    private String image;

    /** FE CartProduct.imageHover — chưa có ảnh hover riêng nên dùng lại ảnh chính. */
    public String getImageHover() {
        return image;
    }
}
