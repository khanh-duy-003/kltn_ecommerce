package com.pk.core.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    // ---- FE BannerItem (snake_case): field tính toán, giữ nguyên field camelCase cũ ----

    @JsonProperty("internal_name")
    public String getInternalNameSnake() {
        return internalName;
    }

    @JsonProperty("media_type")
    public String getMediaTypeSnake() {
        return mediaType;
    }

    @JsonProperty("media_url")
    public String getMediaUrlSnake() {
        return mediaUrl;
    }

    @JsonProperty("media_mobile_url")
    public String getMediaMobileUrlSnake() {
        return mediaMobileUrl != null ? mediaMobileUrl : mediaUrl;
    }

    @JsonProperty("media_poster_url")
    public String getMediaPosterUrlSnake() {
        return mediaPosterUrl;
    }

    @JsonProperty("media_mobile_poster_url")
    public String getMediaMobilePosterUrlSnake() {
        return mediaMobilePosterUrl;
    }

    @JsonProperty("media_link_url")
    public String getMediaLinkUrlSnake() {
        return mediaLinkUrl;
    }

    @JsonProperty("title_color")
    public String getTitleColorSnake() {
        return titleColor;
    }

    @JsonProperty("actions_layout")
    public String getActionsLayoutSnake() {
        return actionsLayout;
    }

    @JsonProperty("overlay_opacity")
    public Double getOverlayOpacitySnake() {
        return overlayOpacity == null ? Double.valueOf(0) : overlayOpacity;
    }
}
