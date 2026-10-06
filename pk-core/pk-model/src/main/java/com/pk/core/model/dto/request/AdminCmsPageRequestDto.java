package com.pk.core.model.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/** Body cho POST /admin/cms/pages (tạo, `blocks` seed nội dung ban đầu - tuỳ chọn) và PUT /admin/cms/pages/{pageId}
 * (cập nhật page + THAY TOÀN BỘ blocks nếu `blocks` != null). Theo spec FE: {name, slug, status, blocks}; `title` vẫn
 * được nhận như bí danh của `name` (tương thích bản cũ). `slug` bỏ trống thì tự sinh từ `name`; `status` bỏ trống =
 * DRAFT (spec: DRAFT | PUBLISHED). */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminCmsPageRequestDto {

    @NotBlank(message = "Tên trang không được để trống")
    @JsonAlias("title")
    private String name;

    private String slug;

    private String status;

    @Valid
    private List<AdminCmsBlockRequestDto> blocks;
}
