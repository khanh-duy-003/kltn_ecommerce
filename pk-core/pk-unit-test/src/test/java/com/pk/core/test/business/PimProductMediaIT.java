package com.pk.core.test.business;

import com.fasterxml.jackson.databind.JsonNode;
import com.pk.core.model.constant.UrlConstant;
import com.pk.core.model.constant.admin.UrlAdminConstant;
import com.pk.core.test.common.IntegrationTestBase;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Thư viện ảnh/video sản phẩm: tạo hoàn toàn qua API admin, kiểm tra ảnh chính -> thumbnail và gallery storefront. */
class PimProductMediaIT extends IntegrationTestBase {

    private static final String V1 = UrlConstant.Common.API + UrlConstant.Common.VERSION;
    private static final String CATALOG = V1 + UrlAdminConstant.Catalog.PRODUCTS;

    @Test
    void mediaLifecycleKeepsSinglePrimaryAndFeedsStorefrontGallery() throws Exception {
        String auth = bearer(adminAccessToken());
        String t = UUID.randomUUID().toString().substring(0, 8);
        long categoryId = body(mvc.perform(jsonRequest(post(V1 + UrlAdminConstant.Catalog.CATEGORIES),
                        Map.of("name", "DM " + t, "slug", "dm-" + t, "sortOrder", 0)).header("Authorization", auth))
                .andExpect(status().isCreated())).get("id").asLong();
        long productId = body(mvc.perform(jsonRequest(post(CATALOG), Map.of(
                        "categoryId", categoryId, "code", "P-" + t, "name", "Nhẫn " + t, "slug", "nhan-" + t,
                        "variants", List.of(Map.of("skuCode", "SKU-" + t, "listPrice", 1_000_000, "onHand", 5,
                                "isDefault", true, "status", "PUBLISHED")))).header("Authorization", auth))
                .andExpect(status().isCreated())).get("id").asLong();
        long skuId = body(mvc.perform(get(CATALOG + "/" + productId + "/variants").header("Authorization", auth)))
                .get(0).get("id").asLong();
        String media = CATALOG + "/" + productId + "/media";

        // url trống -> 400; SKU lạ -> 404; type sai -> 400.
        mvc.perform(jsonRequest(post(media), Map.of("url", " ")).header("Authorization", auth)).andExpect(status().isBadRequest());
        mvc.perform(jsonRequest(post(media), Map.of("url", "/a.jpg", "skuId", 999_999_999L)).header("Authorization", auth))
                .andExpect(status().isNotFound());
        mvc.perform(jsonRequest(post(media), Map.of("url", "/a.jpg", "type", "GIF")).header("Authorization", auth))
                .andExpect(status().isBadRequest());

        // Ảnh đầu tiên tự thành ảnh chính; ảnh 2 yêu cầu isPrimary -> chuyển ảnh chính; luôn đúng 1 ảnh chính.
        long m1 = body(mvc.perform(jsonRequest(post(media), Map.of("url", "/img/1-" + t + ".jpg", "alt", "Ảnh 1", "sortOrder", 1))
                        .header("Authorization", auth))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.primary").value(true))).get("id").asLong();
        long m2 = body(mvc.perform(jsonRequest(post(media), Map.of("url", "/img/2-" + t + ".jpg", "sortOrder", 2, "isPrimary", true))
                        .header("Authorization", auth))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.primary").value(true))).get("id").asLong();
        mvc.perform(jsonRequest(post(media), Map.of("url", "/img/sku-" + t + ".jpg", "skuId", skuId, "sortOrder", 3))
                        .header("Authorization", auth)).andExpect(status().isCreated());
        JsonNode list = body(mvc.perform(get(media).header("Authorization", auth)).andExpect(status().isOk()));
        int primaries = 0;
        for (JsonNode m : list) {
            if (m.get("primary").asBoolean()) {
                primaries++;
                org.junit.jupiter.api.Assertions.assertEquals(String.valueOf(m2), m.get("id").asText());
            }
        }
        org.junit.jupiter.api.Assertions.assertEquals(1, primaries);

        // Thumbnail sản phẩm = ảnh chính; storefront: gallery chung có 2 ảnh, variant có gallery riêng 1 ảnh.
        mvc.perform(patch(CATALOG + "/" + productId + "/publish").header("Authorization", auth)).andExpect(status().isOk());
        mvc.perform(get(V1 + UrlConstant.Product.BASE + "/nhan-" + t))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.thumbnailUrl").value("/img/2-" + t + ".jpg"))
                .andExpect(jsonPath("$.data.gallery.length()").value(2))
                .andExpect(jsonPath("$.data.variants[0].gallery.length()").value(1))
                .andExpect(jsonPath("$.data.variants[0].gallery[0].url").value("/img/sku-" + t + ".jpg"));

        // Xoá ảnh chính -> ảnh chung còn lại được đôn lên và thumbnail cập nhật theo.
        mvc.perform(delete(media + "/" + m2).header("Authorization", auth)).andExpect(status().isNoContent());
        mvc.perform(get(V1 + UrlConstant.Product.BASE + "/nhan-" + t))
                .andExpect(jsonPath("$.data.thumbnailUrl").value("/img/1-" + t + ".jpg"))
                .andExpect(jsonPath("$.data.gallery.length()").value(1));

        // Sửa media + media không thuộc sản phẩm -> 404.
        mvc.perform(jsonRequest(put(media + "/" + m1), Map.of("url", "/img/1b-" + t + ".jpg", "alt", "Mới"))
                        .header("Authorization", auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.url").value("/img/1b-" + t + ".jpg"));
        mvc.perform(delete(media + "/999999999").header("Authorization", auth)).andExpect(status().isNotFound());
    }
}
