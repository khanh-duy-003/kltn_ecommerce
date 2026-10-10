package com.pk.core.test.business;

import com.fasterxml.jackson.databind.JsonNode;
import com.pk.core.model.constant.UrlConstant;
import com.pk.core.model.constant.admin.UrlAdminConstant;
import com.pk.core.test.common.IntegrationTestBase;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Gán thuộc tính (product_attributes) cho SKU: toàn bộ dữ liệu tạo qua API admin, không seed/SQL. */
class PimSkuAttributeIT extends IntegrationTestBase {

    private static final String V1 = UrlConstant.Common.API + UrlConstant.Common.VERSION;
    private static final String CATALOG = V1 + UrlAdminConstant.Catalog.PRODUCTS;

    private static String tag() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    private long createAttribute(String auth, String code, String type, List<String> options) throws Exception {
        return body(mvc.perform(jsonRequest(post(V1 + UrlAdminConstant.Catalog.ATTRIBUTES),
                        Map.of("name", "TT " + code, "code", code, "type", type, "options", options))
                        .header("Authorization", auth))
                .andExpect(status().isCreated())).get("id").asLong();
    }

    @Test
    void bindAttributesToSkuValidateAndShowOnStorefront() throws Exception {
        String auth = bearer(adminAccessToken());
        String t = tag();
        long attrSelect = createAttribute(auth, "gold-" + t, "SELECT", List.of("Vàng 18K", "Vàng 24K"));
        long attrMulti = createAttribute(auth, "style-" + t, "MULTISELECT", List.of("Cổ điển", "Hiện đại", "Tối giản"));
        createAttribute(auth, "note-" + t, "TEXT", List.of());

        long categoryId = body(mvc.perform(jsonRequest(post(V1 + UrlAdminConstant.Catalog.CATEGORIES),
                        Map.of("name", "DM " + t, "slug", "dm-" + t, "sortOrder", 0)).header("Authorization", auth))
                .andExpect(status().isCreated())).get("id").asLong();
        long productId = body(mvc.perform(jsonRequest(post(CATALOG), Map.of(
                        "categoryId", categoryId, "code", "P-" + t, "name", "Nhẫn " + t, "slug", "nhan-" + t,
                        "variants", List.of(Map.of("skuCode", "SKU-" + t, "sizeLabel", "M", "listPrice", 1_000_000,
                                "onHand", 5, "isDefault", true, "status", "PUBLISHED")))).header("Authorization", auth))
                .andExpect(status().isCreated())).get("id").asLong();
        long skuId = body(mvc.perform(get(CATALOG + "/" + productId + "/variants").header("Authorization", auth))
                .andExpect(status().isOk())).get(0).get("id").asLong();
        String path = CATALOG + "/" + productId + "/variants/" + skuId + "/attributes";

        // Giá trị ngoài options -> 400; thuộc tính không tồn tại -> 404; lặp thuộc tính -> 400. Không ghi gì.
        mvc.perform(jsonRequest(put(path), Map.of("values", List.of(Map.of("attributeId", attrSelect, "value", "Bạc"))))
                        .header("Authorization", auth)).andExpect(status().isBadRequest());
        mvc.perform(jsonRequest(put(path), Map.of("values", List.of(Map.of("attributeId", 999_999_999L, "value", "x"))))
                        .header("Authorization", auth)).andExpect(status().isNotFound());
        mvc.perform(jsonRequest(put(path), Map.of("values", List.of(
                        Map.of("attributeId", attrSelect, "value", "Vàng 18K"),
                        Map.of("attributeCode", "gold-" + t, "value", "Vàng 24K")))).header("Authorization", auth))
                .andExpect(status().isBadRequest());
        mvc.perform(get(path).header("Authorization", auth)).andExpect(jsonPath("$.data.length()").value(0));

        // Gán hợp lệ (SELECT theo id, MULTISELECT theo code, TEXT).
        mvc.perform(jsonRequest(put(path), Map.of("values", List.of(
                        Map.of("attributeId", attrSelect, "value", "Vàng 18K"),
                        Map.of("attributeCode", "style-" + t, "values", List.of("Cổ điển", "Tối giản")),
                        Map.of("attributeCode", "note-" + t, "value", "Khắc tên miễn phí")))).header("Authorization", auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.attributeBindings.length()").value(3));
        JsonNode bound = body(mvc.perform(get(path).header("Authorization", auth)).andExpect(status().isOk()));
        org.junit.jupiter.api.Assertions.assertEquals(3, bound.size());
        for (JsonNode b : bound) {
            if (b.get("id").asText().equals(String.valueOf(attrMulti))) {
                org.junit.jupiter.api.Assertions.assertEquals(2, b.get("values").size());
            }
        }

        // Admin xem danh sách biến thể có thuộc tính; storefront (sau publish) trả trong variants[].attributes.
        mvc.perform(get(CATALOG + "/" + productId + "/variants").header("Authorization", auth))
                .andExpect(jsonPath("$.data[0].attributeBindings.length()").value(3));
        mvc.perform(patch(CATALOG + "/" + productId + "/publish").header("Authorization", auth)).andExpect(status().isOk());
        mvc.perform(get(V1 + UrlConstant.Product.BASE + "/nhan-" + t))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.variants[0].attributes['gold-" + t + "']").value("Vàng 18K"))
                .andExpect(jsonPath("$.data.variants[0].attributes.size").value("M"));

        // PUT thay toàn bộ: [] xoá hết.
        mvc.perform(jsonRequest(put(path), Map.of("values", List.of())).header("Authorization", auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.attributeBindings.length()").value(0));

        // SKU không thuộc sản phẩm -> 404.
        mvc.perform(get(CATALOG + "/" + productId + "/variants/999999999/attributes").header("Authorization", auth))
                .andExpect(status().isNotFound());
    }

    @Test
    void assignedAttributeBecomesVariantSelectorWhenSkusDiffer() throws Exception {
        String auth = bearer(adminAccessToken());
        String t = tag();
        long attr = createAttribute(auth, "gold-" + t, "SELECT", List.of("Vàng 18K", "Vàng 24K"));
        long categoryId = body(mvc.perform(jsonRequest(post(V1 + UrlAdminConstant.Catalog.CATEGORIES),
                        Map.of("name", "DM " + t, "slug", "dm-" + t, "sortOrder", 0)).header("Authorization", auth))
                .andExpect(status().isCreated())).get("id").asLong();
        long productId = body(mvc.perform(jsonRequest(post(CATALOG), Map.of(
                        "categoryId", categoryId, "code", "P-" + t, "name", "Nhẫn " + t, "slug", "nhan-" + t,
                        "variants", List.of(Map.of("skuCode", "SKU-" + t, "listPrice", 1_000_000, "onHand", 5,
                                "isDefault", true, "status", "PUBLISHED")))).header("Authorization", auth))
                .andExpect(status().isCreated())).get("id").asLong();
        long sku2 = body(mvc.perform(jsonRequest(post(CATALOG + "/" + productId + "/variants"), Map.of(
                        "skuCode", "SKU2-" + t, "listPrice", 1_200_000, "onHand", 3, "status", "PUBLISHED"))
                        .header("Authorization", auth))
                .andExpect(status().isCreated())).get("id").asLong();
        long sku1 = body(mvc.perform(get(CATALOG + "/" + productId + "/variants").header("Authorization", auth)))
                .get(0).get("id").asLong();
        long first = sku1 == sku2 ? body(mvc.perform(get(CATALOG + "/" + productId + "/variants").header("Authorization", auth))).get(1).get("id").asLong() : sku1;
        mvc.perform(jsonRequest(put(CATALOG + "/" + productId + "/variants/" + first + "/attributes"),
                        Map.of("values", List.of(Map.of("attributeId", attr, "value", "Vàng 18K")))).header("Authorization", auth))
                .andExpect(status().isOk());
        mvc.perform(jsonRequest(put(CATALOG + "/" + productId + "/variants/" + sku2 + "/attributes"),
                        Map.of("values", List.of(Map.of("attributeId", attr, "value", "Vàng 24K")))).header("Authorization", auth))
                .andExpect(status().isOk());
        mvc.perform(patch(CATALOG + "/" + productId + "/publish").header("Authorization", auth)).andExpect(status().isOk());

        mvc.perform(get(V1 + UrlConstant.Product.BASE + "/nhan-" + t))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.variantSelectors[?(@.attribute.code=='gold-" + t + "')].options.length()").value(2));
    }
}
