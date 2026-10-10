package com.pk.core.test.business;

import com.fasterxml.jackson.databind.JsonNode;
import com.pk.core.model.constant.UrlConstant;
import com.pk.core.model.constant.admin.UrlAdminConstant;
import com.pk.core.test.common.IntegrationTestBase;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Vị trí banner tạo qua API admin -> banner gán vào -> storefront render (không dùng repo/SQL để tạo placement). */
class AdminBannerPlacementIT extends IntegrationTestBase {

    private static final String V1 = UrlConstant.Common.API + UrlConstant.Common.VERSION;
    private static final String PLACEMENTS = V1 + UrlAdminConstant.Banner.PLACEMENTS;
    private static final String BANNERS = V1 + UrlAdminConstant.Banner.BANNERS;

    @Test
    void createPlacementThenAssignBannerAndRender() throws Exception {
        String auth = bearer(adminAccessToken());
        String code = "IT_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        JsonNode created = body(mvc.perform(jsonRequest(post(PLACEMENTS),
                        Map.of("code", code.toLowerCase(), "name", "Vị trí IT", "displayType", "grid"))
                        .header("Authorization", auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.code").value(code))
                .andExpect(jsonPath("$.data.displayType").value("GRID")));
        long id = created.get("id").asLong();

        // Trùng code -> 409; displayType sai -> 400; code sai định dạng -> 400.
        mvc.perform(jsonRequest(post(PLACEMENTS), Map.of("code", code, "name", "Trùng")).header("Authorization", auth))
                .andExpect(status().isConflict());
        mvc.perform(jsonRequest(post(PLACEMENTS), Map.of("code", code + "X", "name", "x", "displayType", "MASONRY"))
                        .header("Authorization", auth)).andExpect(status().isBadRequest());
        mvc.perform(jsonRequest(post(PLACEMENTS), Map.of("code", "co dau cach", "name", "x")).header("Authorization", auth))
                .andExpect(status().isBadRequest());

        // Sửa tên + kiểu hiển thị.
        mvc.perform(jsonRequest(patch(PLACEMENTS + "/" + id), Map.of("name", "Đã đổi tên", "displayType", "SINGLE"))
                        .header("Authorization", auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Đã đổi tên"))
                .andExpect(jsonPath("$.data.displayType").value("SINGLE"))
                .andExpect(jsonPath("$.data.code").value(code));
        mvc.perform(jsonRequest(patch(PLACEMENTS + "/999999999"), Map.of("name", "x")).header("Authorization", auth))
                .andExpect(status().isNotFound());

        // Có trong danh sách admin.
        mvc.perform(get(PLACEMENTS).header("Authorization", auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[?(@.code=='" + code + "')]").exists());

        // Gán banner ACTIVE vào vị trí vừa tạo -> storefront render thấy.
        mvc.perform(jsonRequest(post(BANNERS), Map.of("internalName", "Banner " + code, "placementCode", code,
                        "status", "ACTIVE")).header("Authorization", auth))
                .andExpect(status().isOk());
        mvc.perform(get(V1 + UrlConstant.Banner.PLACEMENTS_BY_CODE + "/" + code + "/render"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.displayType").value("SINGLE"))
                .andExpect(jsonPath("$.data.banners.length()").value(1));
    }
}
