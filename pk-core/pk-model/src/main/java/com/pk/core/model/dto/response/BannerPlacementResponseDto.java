package com.pk.core.model.dto.response;

import com.pk.core.model.entity.BannerPlacementEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BannerPlacementResponseDto {

    private String id;
    private String code;
    private String name;
    private String displayType;

    /** Cùng giá trị displayType, theo tên field FE (`type`). */
    public String getType() {
        return displayType;
    }

    public static BannerPlacementResponseDto from(BannerPlacementEntity e) {
        return new BannerPlacementResponseDto(String.valueOf(e.getId()), e.getCode(), e.getName(), e.getDisplayType());
    }
}
