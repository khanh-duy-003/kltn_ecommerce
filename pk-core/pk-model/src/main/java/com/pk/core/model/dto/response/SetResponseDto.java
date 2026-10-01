package com.pk.core.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/** Rút gọn so với Set object đầy đủ trong spec ({id, code, name, slug, description, heroImage,
 * bundleDiscountPercent, status, items[...], seo}) - đây là set SINH RA TỨC THỜI theo yêu cầu, KHÔNG
 * lưu DB (không có domain Set thật), nên bỏ các trường mang tính quản trị (id/slug/status/seo/
 * heroImage riêng, setItemId/allowedVariantIds trong item) - xem AiStylistServiceImpl. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SetResponseDto {

    private String code;
    private String name;
    private String description;
    private BigDecimal bundleDiscountPercent;
    private List<SetItemResponseDto> items;
}
