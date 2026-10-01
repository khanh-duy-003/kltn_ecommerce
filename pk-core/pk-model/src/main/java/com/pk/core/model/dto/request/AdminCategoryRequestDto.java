package com.pk.core.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Tạo/sửa danh mục (Admin Catalog mục J: POST/PUT /admin/catalog/categories). `slug` bỏ trống thì
 * tự sinh từ `name` (SlugUtil, giống ProductServiceImpl khi cần) - CategoryServiceImpl xử lý. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminCategoryRequestDto {

    @NotBlank(message = "Tên danh mục không được để trống")
    private String name;

    private String slug;
    private String description;
    private String imageUrl;
    private Long parentId;
    private int sortOrder;
}
