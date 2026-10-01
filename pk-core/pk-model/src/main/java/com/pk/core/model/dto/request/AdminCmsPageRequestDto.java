package com.pk.core.model.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/** Body cho POST /admin/cms/pages (tạo, `blocks` seed nội dung ban đầu - tuỳ chọn) và PUT
 * /admin/cms/pages/{pageId} (cập nhật page + THAY TOÀN BỘ blocks nếu `blocks` != null - xoá-rồi-tạo
 * lại, cùng cách PromotionServiceImpl.update() thay productIds - mục O spec: "Cập nhật page + block").
 * `slug` bỏ trống thì tự sinh từ `title` (SlugUtil, cùng quy ước Category/Collection/Product). */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminCmsPageRequestDto {

    private String slug;

    @NotBlank(message = "Tiêu đề không được để trống")
    private String title;

    private String status;

    @Valid
    private List<AdminCmsBlockRequestDto> blocks;
}
