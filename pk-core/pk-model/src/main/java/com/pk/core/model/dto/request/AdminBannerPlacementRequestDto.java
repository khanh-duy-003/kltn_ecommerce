package com.pk.core.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Tạo/sửa vị trí banner. Khi tạo cần code; khi sửa chỉ đổi name/displayType (code là khoá để banner trỏ tới nên không đổi). */
@Getter
@Setter
@NoArgsConstructor
public class AdminBannerPlacementRequestDto {

    /** Chữ/số/gạch dưới, ví dụ HOME_HERO; tự chuyển IN HOA. Bắt buộc khi tạo, bỏ qua khi sửa. */
    @Size(max = 60)
    private String code;
    @Size(max = 150)
    private String name;
    /** CAROUSEL | GRID | SINGLE (mặc định CAROUSEL khi tạo). */
    private String displayType;
}
