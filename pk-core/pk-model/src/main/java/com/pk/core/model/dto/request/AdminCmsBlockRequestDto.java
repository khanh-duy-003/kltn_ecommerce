package com.pk.core.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** 1 block trong body AdminCmsPageRequestDto.blocks[] (tạo/thay toàn bộ blocks của trang) hoặc thân
 * riêng cho POST/PUT .../blocks(/{blockId}) (mục O spec). `data` là chuỗi JSON thô do FE tự đóng gói
 * - xem CmsBlockEntity javadoc. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminCmsBlockRequestDto {

    @NotBlank(message = "Loại block không được để trống")
    private String type;

    @PositiveOrZero
    private int sortOrder;

    private String data;
}
