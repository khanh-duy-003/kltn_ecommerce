package com.pk.core.test.identity;

import com.fasterxml.jackson.databind.JsonNode;
import com.pk.core.test.common.IntegrationTestBase;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Luồng SĐT + OTP (/api/storefront/auth/...). application-test.yml đặt app.otp.fixed-code = 123456 nên mọi
 * OTP trong test đều là 123456. Mỗi test dùng SĐT riêng (uniquePhone) vì DB test dùng chung.
 */
class PhoneAuthControllerIT extends IntegrationTestBase {

    private static final String BASE = "/api/v1/storefront/auth";
    private static final String OTP = "123456";

    @Test
    void resolvePhoneUnknownPhoneDoesNotExist() throws Exception {
        postJson(BASE + "/resolve-phone", Map.of("phone", uniquePhone()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.exists").value(false))
                .andExpect(jsonPath("$.data.hasPassword").value(false));
    }

    @Test
    void resolvePhoneKnownPhoneExistsEvenInInternationalFormat() throws Exception {
        String phone = uniquePhone();
        registerCustomer(phone);

        postJson(BASE + "/resolve-phone", Map.of("phone", "+84" + phone.substring(1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.exists").value(true))
                .andExpect(jsonPath("$.data.hasPassword").value(true));
    }

    @Test
    void resolvePhoneWithInvalidPhoneReturns400() throws Exception {
        postJson(BASE + "/resolve-phone", Map.of("phone", "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field=='phone')]").exists());
    }

    @Test
    void fullRegistrationFlowThenLoginWithNewPassword() throws Exception {
        String phone = uniquePhone();
        String email = uniqueEmail();

        postJson(BASE + "/register/send-otp", Map.of("phone", phone))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.success").value(true));

        JsonNode verified = body(postJson(BASE + "/register/verify-otp", Map.of("phone", phone, "otp", OTP)));
        String token = verified.get("registrationToken").asText();

        postJson(BASE + "/register/complete",
                Map.of("registrationToken", token, "fullName", "Nguyen An", "email", email, "password", PASSWORD))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.data.user.phone").value(phone))
                .andExpect(jsonPath("$.data.user.email").value(email))
                .andExpect(jsonPath("$.data.user.roles", hasItem("CUSTOMER")));

        // tài khoản mới đăng nhập được bằng endpoint cũ
        postJson("/api/v1/auth/login", Map.of("phone", phone, "password", PASSWORD))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty());

        // registration token chỉ dùng được một lần
        postJson(BASE + "/register/complete",
                Map.of("registrationToken", token, "fullName", "Nguyen An", "email", uniqueEmail(), "password", PASSWORD))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].code").value("REGISTRATION_TOKEN_INVALID"));
    }

    @Test
    void sendOtpForRegisteredPhoneReturns409() throws Exception {
        String phone = uniquePhone();
        registerCustomer(phone);

        postJson(BASE + "/register/send-otp", Map.of("phone", phone))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errors[0].code").value("PHONE_TAKEN"));
    }

    @Test
    void sendOtpTwiceInARowIsRateLimited() throws Exception {
        String phone = uniquePhone();
        postJson(BASE + "/register/send-otp", Map.of("phone", phone)).andExpect(status().isOk());

        postJson(BASE + "/register/send-otp", Map.of("phone", phone))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.errors[0].code").value("OTP_RATE_LIMITED"));
    }

    @Test
    void verifyWithWrongOtpReturns400() throws Exception {
        String phone = uniquePhone();
        postJson(BASE + "/register/send-otp", Map.of("phone", phone)).andExpect(status().isOk());

        postJson(BASE + "/register/verify-otp", Map.of("phone", phone, "otp", "000000"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].code").value("OTP_INVALID"));
    }

    @Test
    void completeWithoutEmailReturns400() throws Exception {
        postJson(BASE + "/register/complete",
                Map.of("registrationToken", "whatever", "fullName", "An", "password", PASSWORD))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field=='email')]").exists());
    }

    @Test
    void forgotAndResetPasswordFlow() throws Exception {
        String phone = uniquePhone();
        String oldRefreshToken = registerCustomer(phone).get("refreshToken").asText();

        postJson(BASE + "/forgot-password", Map.of("phone", phone))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.success").value(true));

        postJson(BASE + "/reset-password", Map.of("phone", phone, "otp", OTP, "newPassword", "NewPassword2!"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.success").value(true));

        // Đặt lại mật khẩu thu hồi mọi phiên cũ: refresh token cũ không dùng được nữa.
        postJson("/api/v1/auth/refresh", Map.of("refreshToken", oldRefreshToken))
                .andExpect(status().isUnauthorized());

        postJson("/api/v1/auth/login", Map.of("phone", phone, "password", "NewPassword2!"))
                .andExpect(status().isOk());
        postJson("/api/v1/auth/login", Map.of("phone", phone, "password", PASSWORD))
                .andExpect(status().isUnauthorized());

        // OTP đã dùng rồi: không đặt lại được lần nữa
        postJson(BASE + "/reset-password", Map.of("phone", phone, "otp", OTP, "newPassword", "Another3!xx"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].code").value("OTP_INVALID"));
    }

    @Test
    void forgotPasswordForUnknownPhoneStillReturns200() throws Exception {
        postJson(BASE + "/forgot-password", Map.of("phone", uniquePhone()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.success").value(true));
    }
}
