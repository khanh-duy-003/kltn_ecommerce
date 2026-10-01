package com.pk.core.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/** Tạo/sửa thuộc tính sản phẩm (Admin Catalog mục J: POST/PUT /admin/catalog/attributes).
 * `type`: TEXT/SELECT/MULTISELECT (xem {@code ProductAttributeEntity}). `options` chỉ bắt buộc có ý
 * nghĩa khi SELECT/MULTISELECT - CHƯA validate ràng buộc chéo type<->options ở tầng DTO (validate ở
 * service nếu cần chặt hơn), giữ đơn giản cho phạm vi đồ án. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminAttributeRequestDto {

    @NotBlank(message = "Tên thuộc tính không được để trống")
    private String name;

    @NotBlank(message = "Code thuộc tính không được để trống")
    private String code;

    @NotBlank(message = "Loại thuộc tính không được để trống")
    private String type;

    private List<@NotEmpty String> options;
}
