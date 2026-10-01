package com.pk.core.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Body cho POST/PATCH /admin/badge-templates(/{badgeId}) (mục P spec). Spec chỉ liệt kê GET/PATCH/
 * DELETE theo id (không có POST list/create rõ ràng) - Claude THÊM GET(list)/POST(create) hợp lý vì
 * phải có cách tạo mẫu nhãn trước khi sửa/xoá được (xem RULE-CODE.md mục "Badge admin - bổ khuyết
 * spec thiếu"). PATCH dùng chung DTO này, coi như cập nhật toàn bộ field (không phải partial patch
 * thật sự - cùng cách các PUT khác trong dự án). */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminBadgeTemplateRequestDto {

    @NotBlank(message = "Tên mẫu nhãn không được để trống")
    private String name;

    @NotBlank(message = "Nội dung nhãn không được để trống")
    private String labelText;

    private String color;
}
