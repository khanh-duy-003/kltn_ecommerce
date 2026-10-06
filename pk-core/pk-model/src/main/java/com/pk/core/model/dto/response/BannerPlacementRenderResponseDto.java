package com.pk.core.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/** Kết quả render một vị trí banner: thông tin vị trí + các banner ACTIVE theo thứ tự hiển thị. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BannerPlacementRenderResponseDto {

    private String code;
    private String name;
    private String displayType;
    private List<BannerResponseDto> banners;
}
