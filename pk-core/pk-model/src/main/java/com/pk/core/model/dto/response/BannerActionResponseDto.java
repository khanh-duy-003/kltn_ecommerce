package com.pk.core.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Một nút bấm của banner (spec FE). */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BannerActionResponseDto {

    private String actionType;
    private String actionTarget;
    private String ctaText;
    private String ctaBg;
    private String ctaColor;
}
