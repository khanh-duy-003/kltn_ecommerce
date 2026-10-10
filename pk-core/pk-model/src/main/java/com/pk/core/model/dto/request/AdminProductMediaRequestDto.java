package com.pk.core.model.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Thêm/sửa ảnh-video sản phẩm. url là đường dẫn ảnh đã có (từ API upload hoặc CDN). */
@Getter
@Setter
@NoArgsConstructor
public class AdminProductMediaRequestDto {

    @NotBlank
    @Size(max = 500)
    private String url;
    @Size(max = 200)
    private String alt;
    /** IMAGE (mặc định) | VIDEO. */
    private String type;
    private Integer sortOrder;
    /** Bỏ trống = media chung của sản phẩm; có giá trị = chỉ hiển thị cho SKU này. */
    @JsonAlias({"variationId", "variantId"})
    private Long skuId;
    @JsonAlias("isPrimary")
    private Boolean primary;
}
