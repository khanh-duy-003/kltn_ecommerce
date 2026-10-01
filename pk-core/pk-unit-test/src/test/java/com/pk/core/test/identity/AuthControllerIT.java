package com.pk.core.test.identity;

import com.fasterxml.jackson.databind.JsonNode;
import com.pk.core.test.common.IntegrationTestBase;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerIT extends IntegrationTestBase {

    @Test
    void registerReturns201WithTokensAndCustomerRole() throws Exception {
        String email = uniqueEmail();
        postJson("/api/auth/register", Map.of("email", email, "password", PASSWORD, "fullName", "An"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.data.refreshToken").isNotEmpty())
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.data.user.email").value(email))
                .andExpect(jsonPath("$.data.user.roles", hasItem("CUSTOMER")));
    }

    @Test
    void registerDuplicateEmailReturns409EvenWithDifferentCase() throws Exception {
        String email = uniqueEmail();
        registerCustomer(email);

        postJson("/api/auth/register", Map.of("email", email.toUpperCase(), "password", PASSWORD, "fullName", "Dup"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errors[0].code").value("EMAIL_TAKEN"));
    }

    @Test
    void registerWithInvalidBodyReturns400WithFieldErrors() throws Exception {
        postJson("/api/auth/register", Map.of("email", "not-an-email", "password", "123", "fullName", "An"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field=='email')]").exists())
                .andExpect(jsonPath("$.errors[?(@.field=='password')]").exists());
    }

    @Test
    void loginWithCorrectPasswordReturnsTokens() throws Exception {
        String email = uniqueEmail();
        registerCustomer(email);

        postJson("/api/auth/login", Map.of("email", email, "password", PASSWORD))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty());
    }

    @Test
    void loginWithWrongPasswordReturns401() throws Exception {
        String email = uniqueEmail();
        registerCustomer(email);

        postJson("/api/auth/login", Map.of("email", email, "password", "WrongPassword1"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errors[0].code").value("INVALID_CREDENTIALS"));
    }

    @Test
    void meWithoutTokenReturns401Json() throws Exception {
        mvc.perform(get("/api/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errors[0].code").value("UNAUTHORIZED"));
    }

    @Test
    void meWithGarbageTokenReturns401() throws Exception {
        mvc.perform(get("/api/me").header("Authorization", bearer("not.a.jwt")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void meWithValidTokenReturnsProfile() throws Exception {
        String email = uniqueEmail();
        String token = registerCustomer(email).get("accessToken").asText();

        mvc.perform(get("/api/me").header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value(email))
                .andExpect(jsonPath("$.data.roles", hasItem("CUSTOMER")));
    }

    @Test
    void refreshRotatesTokenAndOldTokenIsRejected() throws Exception {
        String first = registerCustomer(uniqueEmail()).get("refreshToken").asText();

        JsonNode rotated = body(postJson("/api/auth/refresh", Map.of("refreshToken", first))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty()));
        String second = rotated.get("refreshToken").asText();

        // Token cũ đã dùng rồi: bị từ chối...
        postJson("/api/auth/refresh", Map.of("refreshToken", first))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errors[0].code").value("REFRESH_REUSED"));

        // ...và việc dùng lại bị coi là dấu hiệu bị đánh cắp: token mới của cùng phiên cũng bị thu hồi.
        postJson("/api/auth/refresh", Map.of("refreshToken", second))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void refreshWithUnknownTokenReturns401() throws Exception {
        postJson("/api/auth/refresh", Map.of("refreshToken", "does-not-exist"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errors[0].code").value("REFRESH_INVALID"));
    }

    @Test
    void logoutRevokesRefreshToken() throws Exception {
        String refresh = registerCustomer(uniqueEmail()).get("refreshToken").asText();

        postJson("/api/auth/logout", Map.of("refreshToken", refresh)).andExpect(status().isNoContent());

        postJson("/api/auth/refresh", Map.of("refreshToken", refresh)).andExpect(status().isUnauthorized());
    }

    @Test
    void customerCannotAccessAdminEndpoints() throws Exception {
        mvc.perform(get("/api/admin/anything").header("Authorization", bearer(customerAccessToken())))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errors[0].code").value("FORBIDDEN"));
    }
}
