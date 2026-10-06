package com.pk.core.model.dto.response;

import com.pk.core.model.entity.BannerEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Banner theo spec FE (id là chuỗi). {@code placementCode}/{@code sortOrder} là mở rộng ngoài spec; ở response render
 * storefront hai field này cũng có mặt (vô hại). Không gắn audit (spec không có).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BannerResponseDto {

    private String id;
    private String internalName;
    private String mediaUrl;
    private String mediaMobileUrl;
    private String mediaLinkUrl;
    private String mediaPosterUrl;
    private String mediaMobilePosterUrl;
    private String mediaFit;
    private String mediaType;
    private String layout;
    private String title;
    private String subtitle;
    private String titleColor;
    private String actionsLayout;
    private List<BannerActionResponseDto> actions;
    private Double overlayOpacity;
    private String status;
    private String placementCode;
    private int sortOrder;

    public static BannerResponseDto from(BannerEntity e, List<BannerActionResponseDto> actions) {
        return new BannerResponseDto(String.valueOf(e.getId()), e.getInternalName(), e.getMediaUrl(),
                e.getMediaMobileUrl(), e.getMediaLinkUrl(), e.getMediaPosterUrl(), e.getMediaMobilePosterUrl(),
                e.getMediaFit(), e.getMediaType(), e.getLayout(), e.getTitle(), e.getSubtitle(), e.getTitleColor(),
                e.getActionsLayout(), actions,
                e.getOverlayOpacity() == null ? null : e.getOverlayOpacity().doubleValue(),
                e.getStatus(), e.getPlacementCode(), e.getSortOrder());
    }
}
