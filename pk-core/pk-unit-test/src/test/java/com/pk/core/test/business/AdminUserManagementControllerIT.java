package com.pk.core.test.business;

import com.pk.core.model.constant.UrlConstant;
import com.fasterxml.jackson.databind.JsonNode;
import com.pk.core.model.constant.admin.UrlAdminConstant;
import com.pk.core.test.common.IntegrationTestBase;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Quản lý tài khoản/vai trò (chỉ ADMIN) và phân quyền khu quản trị theo vai trò nhân viên. */
class AdminUserManagementControllerIT extends IntegrationTestBase {

    private static final String ORDERS = UrlConstant.Common.API + UrlConstant.Common.VERSION + UrlAdminConstant.Common.BASE + "/orders";
    private static final String CATALOG = UrlConstant.Common.API + UrlConstant.Common.VERSION + UrlAdminConstant.Catalog.PRODUCTS;

    private String adminAuth() throws Exception {
        return bearer(adminAccessToken());
    }

    private JsonNode createStaff(String phone, String role) throws Exception {
        return body(mvc.perform(jsonRequest(post(UrlConstant.Common.API + UrlConstant.Common.VERSION + UrlAdminConstant.Access.USERS), Map.of("fullName", "Nhân viên",
                        "phone", phone, "password", PASSWORD, "roles", List.of(role)))
                        .header("Authorization", adminAuth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.phone").value(phone))
                .andExpect(jsonPath("$.data.roles[0]").value(role)));
    }

    private String login(String phone) throws Exception {
        return body(postJson("/api/v1/auth/login", Map.of("phone", phone, "password", PASSWORD))).get("accessToken").asText();
    }

    @Test
    void onlyAdminCanManageUsers() throws Exception {
        mvc.perform(get(UrlConstant.Common.API + UrlConstant.Common.VERSION + UrlAdminConstant.Access.USERS)).andExpect(status().isUnauthorized());
        mvc.perform(get(UrlConstant.Common.API + UrlConstant.Common.VERSION + UrlAdminConstant.Access.USERS).header("Authorization", bearer(customerAccessToken())))
                .andExpect(status().isForbidden());

        String phone = uniquePhone();
        createStaff(phone, "ORDER_MANAGER");
        mvc.perform(get(UrlConstant.Common.API + UrlConstant.Common.VERSION + UrlAdminConstant.Access.USERS).header("Authorization", bearer(login(phone))))
                .andExpect(status().isForbidden());
    }

    @Test
    void createValidatesPhoneRoleAndDuplicates() throws Exception {
        String phone = uniquePhone();
        createStaff(phone, "CATALOG_MANAGER");

        mvc.perform(jsonRequest(post(UrlConstant.Common.API + UrlConstant.Common.VERSION + UrlAdminConstant.Access.USERS), Map.of("fullName", "Trùng", "phone", phone,
                        "password", PASSWORD, "roles", List.of("ADMIN"))).header("Authorization", adminAuth()))
                .andExpect(status().isConflict());
        mvc.perform(jsonRequest(post(UrlConstant.Common.API + UrlConstant.Common.VERSION + UrlAdminConstant.Access.USERS), Map.of("fullName", "Sai vai trò",
                        "phone", uniquePhone(), "password", PASSWORD, "roles", List.of("SUPERMAN")))
                        .header("Authorization", adminAuth()))
                .andExpect(status().isBadRequest());
        mvc.perform(jsonRequest(post(UrlConstant.Common.API + UrlConstant.Common.VERSION + UrlAdminConstant.Access.USERS), Map.of("fullName", "Sai SĐT",
                        "phone", "12345", "password", PASSWORD, "roles", List.of("ADMIN")))
                        .header("Authorization", adminAuth()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void staffRolesOnlyReachTheirOwnAdminGroups() throws Exception {
        String orderPhone = uniquePhone();
        createStaff(orderPhone, "ORDER_MANAGER");
        String orderToken = bearer(login(orderPhone));
        mvc.perform(get(ORDERS).header("Authorization", orderToken)).andExpect(status().isOk());
        mvc.perform(get(CATALOG).header("Authorization", orderToken)).andExpect(status().isForbidden());

        String catalogPhone = uniquePhone();
        createStaff(catalogPhone, "CATALOG_MANAGER");
        String catalogToken = bearer(login(catalogPhone));
        mvc.perform(get(CATALOG).header("Authorization", catalogToken)).andExpect(status().isOk());
        mvc.perform(get(ORDERS).header("Authorization", catalogToken)).andExpect(status().isForbidden());

        // Khu quản trị: nhân viên đăng nhập được qua /admin/auth/login, khách hàng thì 403.
        postJson(UrlConstant.Common.API + UrlConstant.Common.VERSION + UrlAdminConstant.Auth.LOGIN, Map.of("phone", orderPhone, "password", PASSWORD))
                .andExpect(status().isOk());
        String customerPhone = uniquePhone();
        registerCustomer(customerPhone);
        postJson(UrlConstant.Common.API + UrlConstant.Common.VERSION + UrlAdminConstant.Auth.LOGIN, Map.of("phone", customerPhone, "password", PASSWORD))
                .andExpect(status().isForbidden());
    }

    @Test
    void assignRolesReplacesRolesAndRevokesRefreshTokens() throws Exception {
        String phone = uniquePhone();
        JsonNode created = createStaff(phone, "ORDER_MANAGER");
        long id = created.get("id").asLong();
        String refresh = body(postJson("/api/v1/auth/login", Map.of("phone", phone, "password", PASSWORD)))
                .get("refreshToken").asText();

        mvc.perform(jsonRequest(put(UrlConstant.Common.API + UrlConstant.Common.VERSION + UrlAdminConstant.Access.USERS + "/" + id + "/roles"),
                        Map.of("roles", List.of("CATALOG_MANAGER"))).header("Authorization", adminAuth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.roles.length()").value(1))
                .andExpect(jsonPath("$.data.roles[0]").value("CATALOG_MANAGER"));

        postJson("/api/v1/auth/refresh", Map.of("refreshToken", refresh)).andExpect(status().isUnauthorized());
        mvc.perform(get(CATALOG).header("Authorization", bearer(login(phone)))).andExpect(status().isOk());
    }

    @Test
    void lockedAccountCannotLoginAndAdminCannotLockSelf() throws Exception {
        String phone = uniquePhone();
        long id = createStaff(phone, "ORDER_MANAGER").get("id").asLong();

        mvc.perform(jsonRequest(patch(UrlConstant.Common.API + UrlConstant.Common.VERSION + UrlAdminConstant.Access.USERS + "/" + id), Map.of("enabled", false,
                        "fullName", "Đã khoá")).header("Authorization", adminAuth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.enabled").value(false))
                .andExpect(jsonPath("$.data.fullName").value("Đã khoá"));
        postJson("/api/v1/auth/login", Map.of("phone", phone, "password", PASSWORD)).andExpect(status().isUnauthorized());

        // Tự khoá / tự gỡ ADMIN bị chặn.
        String adminPhone = uniquePhone();
        long adminId = createStaff(adminPhone, "ADMIN").get("id").asLong();
        String adminToken = bearer(login(adminPhone));
        mvc.perform(jsonRequest(patch(UrlConstant.Common.API + UrlConstant.Common.VERSION + UrlAdminConstant.Access.USERS + "/" + adminId), Map.of("enabled", false))
                        .header("Authorization", adminToken))
                .andExpect(status().isConflict());
        mvc.perform(jsonRequest(put(UrlConstant.Common.API + UrlConstant.Common.VERSION + UrlAdminConstant.Access.USERS + "/" + adminId + "/roles"),
                        Map.of("roles", List.of("CUSTOMER"))).header("Authorization", adminToken))
                .andExpect(status().isConflict());
    }

    @Test
    void rolesListShowsPermissions() throws Exception {
        createStaff(uniquePhone(), "ORDER_MANAGER");
        mvc.perform(get(UrlConstant.Common.API + UrlConstant.Common.VERSION + UrlAdminConstant.Access.ROLES).header("Authorization", adminAuth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[?(@.name=='ORDER_MANAGER')].permissions[0]").exists());
    }
}
