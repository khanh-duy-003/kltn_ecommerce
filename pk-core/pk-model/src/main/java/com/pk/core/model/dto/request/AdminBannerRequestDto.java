package com.pk.core.model.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.groups.Default;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Body tạo (POST) và cập nhật một phần (PATCH) banner. Tạo dùng nhóm {@link OnCreate} (bắt buộc internalName); PATCH
 * dùng nhóm mặc định nên mọi field đều tuỳ chọn - field nào null thì giữ nguyên. {@code placementCode} và
 * {@code sortOrder} là mở rộng NGOÀI spec FE (gắn banner vào vị trí hiển thị để render theo mã).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminBannerRequestDto {

    /** Nhóm validate cho POST: gồm mọi ràng buộc mặc định + @NotBlank internalName. */
    public interface OnCreate extends Default {
    }

    @NotBlank(groups = OnCreate.class)
    @Size(max = 150)
    private String internalName;

    @Size(max = 500)
    private String mediaUrl;

    @Size(max = 500)
    private String mediaMobileUrl;

    @Size(max = 500)
    private String mediaLinkUrl;

    @Size(max = 500)
    private String mediaPosterUrl;

    @Size(max = 500)
    private String mediaMobilePosterUrl;

    @Pattern(regexp = "^(cover|contain|fill|none)$", message = "mediaFit không hợp lệ")
    private String mediaFit;

    @Pattern(regexp = "^(GIF|IMAGE|VIDEO|VIRTUAL)$", message = "mediaType không hợp lệ")
    private String mediaType;

    @Pattern(regexp = "^(RIGHT_TOP|CENTER_TOP|LEFT_TOP|RIGHT_CENTER|CENTER|LEFT_CENTER|RIGHT_BOTTOM|CENTER_BOTTOM|LEFT_BOTTOM)$",
            message = "layout không hợp lệ")
    private String layout;

    @Size(max = 200)
    private String title;

    @Size(max = 300)
    private String subtitle;

    @Size(max = 30)
    private String titleColor;

    @Pattern(regexp = "^(STACK|INLINE)$", message = "actionsLayout không hợp lệ")
    private String actionsLayout;

    @Size(max = 10)
    @Valid
    private List<BannerActionRequestDto> actions;

    @DecimalMin("0")
    @DecimalMax("1")
    private Double overlayOpacity;

    @Pattern(regexp = "^(DRAFT|ACTIVE|INACTIVE|ARCHIVED)$", message = "status không hợp lệ")
    private String status;

    /** Mở rộng ngoài spec: mã vị trí hiển thị (HOME_HERO...). Chuỗi rỗng = gỡ khỏi vị trí. */
    @Size(max = 60)
    private String placementCode;

    /** Mở rộng ngoài spec: thứ tự trong vị trí (nhỏ hiện trước). */
    private Integer sortOrder;
}
