package com.pk.core.test.business;

import com.pk.core.model.constant.UrlConstant;
import com.pk.core.model.constant.admin.UrlAdminConstant;
import com.pk.core.test.common.IntegrationTestBase;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** GET /admin/users, GET /admin/roles, GET/POST /admin/pre-orders - chỉ admin. */
class AdminAccessPreOrderControllerIT extends IntegrationTestBase {

    private static final String USERS = UrlConstant.Common.API + UrlConstant.Common.VERSION + UrlAdminConstant.Access.USERS;
    private static final String ROLES = UrlConstant.Common.API + UrlConstant.Common.VERSION + UrlAdminConstant.Access.ROLES;
    private static final String PRE_ORDERS = UrlConstant.Common.API + UrlConstant.Common.VERSION + UrlAdminConstant.PreOrder.BASE;

    @Test
    void endpointsRequireAdminRole() throws Exception {
        for (String path : new String[] {USERS, ROLES, PRE_ORDERS}) {
            mvc.perform(get(path)).andExpect(status().isUnauthorized());
            mvc.perform(get(path).header("Authorization", bearer(customerAccessToken()))).andExpect(status().isForbidden());
        }
    }

    @Test
    void usersListShowsRolesAndNeverLeaksPasswordHash() throws Exception {
        String phone = uniquePhone();
        registerCustomer(phone);

        mvc.perform(get(USERS).header("Authorization", bearer(adminAccessToken())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[?(@.phone=='" + phone + "' && @.roles[0]=='CUSTOMER')]").exists())
                .andExpect(content().string(not(containsString("passwordHash"))))
                .andExpect(content().string(not(containsString("password_hash"))));
    }

    @Test
    void rolesListContainsAdminAndCustomer() throws Exception {
        mvc.perform(get(ROLES).header("Authorization", bearer(adminAccessToken())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[*].name").value(hasItems("ADMIN", "CUSTOMER")));
    }

    @Test
    void preOrderCreateThenListNewestFirst() throws Exception {
        String admin = bearer(adminAccessToken());

        mvc.perform(jsonRequest(post(PRE_ORDERS), Map.of("enabled", true, "message", "Đặt trước đợt 1"))
                        .header("Authorization", admin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.enabled").value(true))
                .andExpect(jsonPath("$.data.message").value("Đặt trước đợt 1"));
        mvc.perform(jsonRequest(post(PRE_ORDERS), Map.of("enabled", false, "message", "Đóng đặt trước"))
                        .header("Authorization", admin))
                .andExpect(status().isOk());

        mvc.perform(get(PRE_ORDERS).header("Authorization", admin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].enabled").value(false))
                .andExpect(jsonPath("$.data[0].message").value("Đóng đặt trước"));
    }

    @Test
    void preOrderCreateWithoutEnabledReturns400() throws Exception {
        mvc.perform(jsonRequest(post(PRE_ORDERS), Map.of("message", "thiếu enabled"))
                        .header("Authorization", bearer(adminAccessToken())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field=='enabled')]").exists());
    }
}
