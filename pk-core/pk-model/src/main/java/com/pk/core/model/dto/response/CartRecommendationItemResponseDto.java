package com.pk.core.model.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Sản phẩm gợi ý trong giỏ (đại diện bởi một SKU của sản phẩm). sellingPriceAfterTaxMinor là CHUỖI theo spec FE. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartRecommendationItemResponseDto {

    /** Id SKU đại diện. */
    private String id;
    private String slug;
    private String name;
    private String productId;
    private String sellingPriceAfterTaxMinor;
    private int stock;
    private String status;
    /** FE CartRecommendationProduct.compareAtPriceAfterTaxMinor (chuỗi; "0" nếu không giảm giá). */
    private String compareAtPriceAfterTaxMinor;
    /** Ảnh sản phẩm — chỉ dùng để dựng "product" lồng. */
    @JsonIgnore
    private String image;

    public Map<String, Object> getCustomerDisplayPrice() {
        long selling = parse(sellingPriceAfterTaxMinor);
        long compare = parse(compareAtPriceAfterTaxMinor);
        boolean d = compare > selling;
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("currency", "VND");
        m.put("compareAtPriceAfterTaxMinor", d ? compare : null);
        m.put("sellingPriceAfterTaxMinor", selling);
        m.put("discountPercent", d ? (int) Math.round((compare - selling) * 100.0 / compare) : null);
        m.put("hasDiscount", d);
        return m;
    }

    public Map<String, Object> getProduct() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", productId);
        m.put("name", name);
        m.put("image", image);
        return m;
    }

    public List<Object> getAttributes() {
        return new ArrayList<>();
    }

    private static long parse(String v) {
        try {
            return v == null ? 0L : Long.parseLong(v);
        } catch (NumberFormatException e) {
            return 0L;
        }
    }
}
