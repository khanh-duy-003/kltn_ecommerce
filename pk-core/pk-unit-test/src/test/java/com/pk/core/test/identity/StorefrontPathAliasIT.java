package com.pk.core.test.identity;

import com.pk.core.test.common.IntegrationTestBase;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Đường dẫn theo spec FE (/api/storefront/auth/login, /api/storefront/me) là alias của /api/auth/login, /api/me. */
class StorefrontPathAliasIT extends IntegrationTestBase {

    @Test
    void loginAndMeWorkOnStorefrontPaths() throws Exception {
        String phone = uniquePhone();
        registerCustomer(phone);

        String token = body(postJson("/api/storefront/auth/login", Map.of("phone", phone, "password", PASSWORD))
                .andExpect(status().isOk())).get("accessToken").asText();

        mvc.perform(get("/api/storefront/me").header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.phone").value(phone));
        mvc.perform(get("/api/storefront/me")).andExpect(status().isUnauthorized());
    }
}
