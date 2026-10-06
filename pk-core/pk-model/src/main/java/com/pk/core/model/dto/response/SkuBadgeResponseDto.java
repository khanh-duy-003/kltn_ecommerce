package com.pk.core.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/** 1 phần tử của GET /storefront/badge: SKU và các nhãn đang áp (hiện tại tối đa 1 nhãn/SKU). */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SkuBadgeResponseDto {

    private String skuId;
    private List<BadgeTemplateResponseDto> badges;
}
