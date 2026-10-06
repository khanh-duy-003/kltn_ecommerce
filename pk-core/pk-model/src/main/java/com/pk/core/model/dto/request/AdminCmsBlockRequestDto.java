package com.pk.core.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

/** 1 block theo spec FE (CmsBlockInput): dùng trong AdminCmsPageRequestDto.blocks[] và làm body POST/PUT
 * .../blocks(/{blockId}). `type` thuộc 8 loại của spec; `config` là object tự do (BANNER cần placementCode,
 * PRODUCT_CAROUSEL cần productIds hoặc collectionSlug - xem CmsServiceImpl). `targetSegment` tuỳ chọn. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminCmsBlockRequestDto {

    @NotBlank(message = "Loại block không được để trống")
    private String type;

    @NotNull(message = "sortOrder không được để trống")
    @PositiveOrZero
    private Integer sortOrder;

    @NotNull(message = "config không được để trống")
    private Map<String, Object> config;

    private String targetSegment;
}
