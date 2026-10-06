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
        String phone = uniquePhone();
        String email = uniqueEmail();
        postJson("/api/auth/register", Map.of("phone", phone, "email", email, "password", PASSWORD, "fullName", "An"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.data.refreshToken").isNotEmpty())
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.data.user.phone").value(phone))
                .andExpect(jsonPath("$.data.user.email").value(email))
                .andExpect(jsonPath("$.data.user.roles", hasItem("CUSTOMER")));
    }

    @Test
    void registerWithoutEmailIsAllowed() throws Exception {
        String phone = uniquePhone();
        postJson("/api/auth/register", Map.of("phone", phone, "password", PASSWORD, "fullName", "An"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.user.phone").value(phone));
    }

    @Test
    void registerDuplicatePhoneReturns409EvenInInternationalFormat() throws Exception {
        String phone = uniquePhone();
        registerCustomer(phone);

        // cùng một số nhưng viết dạng +84: phải bị coi là trùng
        String international = "+84" + phone.substring(1);
        postJson("/api/auth/register", Map.of("phone", international, "password", PASSWORD, "fullName", "Dup"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errors[0].code").value("PHONE_TAKEN"));
    }

    @Test
    void registerDuplicateEmailReturns409EvenWithDifferentCase() throws Exception {
        String email = uniqueEmail();
        postJson("/api/auth/register", Map.of("phone", uniquePhone(), "email", email, "password", PASSWORD, "fullName", "An"))
                .andExpect(status().isCreated());

        postJson("/api/auth/register", Map.of("phone", uniquePhone(), "email", email.toUpperCase(), "password", PASSWORD, "fullName", "Dup"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errors[0].code").value("EMAIL_TAKEN"));
    }

    @Test
    void registerWithInvalidBodyReturns400WithFieldErrors() throws Exception {
        postJson("/api/auth/register", Map.of("phone", "abc", "email", "not-an-email", "password", "123", "fullName", "An"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field=='phone')]").exists())
                .andExpect(jsonPath("$.errors[?(@.field=='email')]").exists())
                .andExpect(jsonPath("$.errors[?(@.field=='password')]").exists());
    }

    @Test
    void registerWithoutPhoneReturns400() throws Exception {
        postJson("/api/auth/register", Map.of("email", uniqueEmail(), "password", PASSWORD, "fullName", "An"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field=='phone')]").exists());
    }

    @Test
    void loginWithCorrectPasswordReturnsTokens() throws Exception {
        String phone = uniquePhone();
        registerCustomer(phone);

        postJson("/api/auth/login", Map.of("phone", phone, "password", PASSWORD))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty());
    }

    @Test
    void loginAcceptsInternationalPhoneFormat() throws Exception {
        String phone = uniquePhone();
        registerCustomer(phone);

        postJson("/api/auth/login", Map.of("phone", "+84" + phone.substring(1), "password", PASSWORD))
                .andExpect(status().isOk());
    }

    @Test
    void loginWithWrongPasswordReturns401() throws Exception {
        String phone = uniquePhone();
        registerCustomer(phone);

        postJson("/api/auth/login", Map.of("phone", phone, "password", "WrongPassword1"))
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
        String phone = uniquePhone();
        String token = registerCustomer(phone).get("accessToken").asText();

        mvc.perform(get("/api/me").header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.phone").value(phone))
                .andExpect(jsonPath("$.data.roles", hasItem("CUSTOMER")));
    }

    @Test
    void refreshRotatesTokenAndOldTokenIsRejected() throws Exception {
        String first = registerCustomer(uniquePhone()).get("refreshToken").asText();

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
        String refresh = registerCustomer(uniquePhone()).get("refreshToken").asText();

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
