package com.pk.core.model.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Một nút bấm của banner (spec FE). */
@JsonIgnoreProperties(ignoreUnknown = true)
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

    // ---- FE BannerAction (snake_case): field tính toán, giữ nguyên field camelCase cũ ----

    @JsonProperty("action_type")
    public String getActionTypeSnake() {
        return actionType;
    }

    @JsonProperty("action_target")
    public String getActionTargetSnake() {
        return actionTarget;
    }

    @JsonProperty("cta_text")
    public String getCtaTextSnake() {
        return ctaText;
    }

    @JsonProperty("cta_bg")
    public String getCtaBgSnake() {
        return ctaBg;
    }

    @JsonProperty("cta_color")
    public String getCtaColorSnake() {
        return ctaColor;
    }
}
