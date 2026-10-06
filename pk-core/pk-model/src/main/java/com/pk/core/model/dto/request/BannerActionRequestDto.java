package com.pk.core.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Một nút bấm của banner (spec FE). LINK được nhận như URL (query lọc của spec dùng tên LINK, body dùng URL). */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BannerActionRequestDto {

    @NotBlank
    @Pattern(regexp = "^(URL|LINK|NEWS|POPUP|PRODUCT|CATEGORY|COLLECTION)$", message = "actionType không hợp lệ")
    private String actionType;

    @Size(max = 500)
    private String actionTarget;

    @Size(max = 100)
    private String ctaText;

    @Size(max = 30)
    private String ctaBg;

    @Size(max = 30)
    private String ctaColor;
}
