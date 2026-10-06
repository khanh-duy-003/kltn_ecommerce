package com.pk.core.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

/** Block của trang storefront: ngoài {id, type, sortOrder, config} còn có `content` = dữ liệu đã được backend nạp sẵn
 * cho client: BANNER -> {layout, placement, banners[]}; PRODUCT_CAROUSEL -> {header, products[]}; loại khác null. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CmsStorefrontBlockResponseDto {

    private String id;
    private String type;
    private int sortOrder;
    private Map<String, Object> config;
    private Map<String, Object> content;
}
