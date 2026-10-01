package com.pk.core.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Tạo/sửa bộ sưu tập (Admin Catalog mục J: POST/PUT /admin/catalog/collections). `slug` bỏ trống
 * thì tự sinh từ `name`. `status` bỏ trống thì giữ DRAFT (CollectionEntity mặc định). */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminCollectionRequestDto {

    @NotBlank(message = "Tên bộ sưu tập không được để trống")
    private String name;

    private String slug;
    private String description;
    private String heroImageUrl;
    private String status;
}
