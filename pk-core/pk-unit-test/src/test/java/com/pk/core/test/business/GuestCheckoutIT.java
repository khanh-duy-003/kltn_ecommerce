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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Khách vãng lai: giỏ -> báo giá -> đặt hàng -> tra cứu bằng mã đơn + SĐT, không có token. Dữ liệu tạo qua API admin. */
class GuestCheckoutIT extends IntegrationTestBase {

    private static final String V1 = UrlConstant.Common.API + UrlConstant.Common.VERSION;
    private static final String CATALOG = V1 + UrlAdminConstant.Catalog.PRODUCTS;
    private static final String PHONE = "0912345678";

    @Test
    void guestCanQuotePlaceOrderAndLookupWithPhoneOnly() throws Exception {
        String t = UUID.randomUUID().toString().substring(0, 8);
        String admin = bearer(adminAccessToken());
        long categoryId = body(mvc.perform(jsonRequest(post(V1 + UrlAdminConstant.Catalog.CATEGORIES),
                        Map.of("name", "DM " + t, "slug", "dm-" + t, "sortOrder", 0)).header("Authorization", admin))
                .andExpect(status().isCreated())).get("id").asLong();
        long productId = body(mvc.perform(jsonRequest(post(CATALOG), Map.of(
                        "categoryId", categoryId, "code", "P-" + t, "name", "Nhẫn " + t, "slug", "nhan-" + t,
                        "variants", List.of(Map.of("skuCode", "SKU-" + t, "listPrice", 1_000_000, "onHand", 5,
                                "isDefault", true, "status", "PUBLISHED")))).header("Authorization", admin))
                .andExpect(status().isCreated())).get("id").asLong();
        long skuId = body(mvc.perform(get(CATALOG + "/" + productId + "/variants").header("Authorization", admin)))
                .get(0).get("id").asLong();
        mvc.perform(patch(CATALOG + "/" + productId + "/publish").header("Authorization", admin)).andExpect(status().isOk());

        // Giỏ khách (không token) -> lấy guestId.
        String guestId = body(mvc.perform(jsonRequest(post(V1 + UrlConstant.Cart.BASE), Map.of("clearAll", false, "items",
                        List.of(Map.of("variationId", String.valueOf(skuId), "quantity", 2)))))
                .andExpect(status().isOk())).path("guestId").asText(null);

        Map<String, Object> req = new java.util.HashMap<>(Map.of("recipientName", "Khách A", "phone", PHONE,
                "province", "HCM", "addressLine", "1 Lê Lợi", "shippingMethod", "STANDARD", "paymentMethod", "COD",
                "items", List.of(Map.of("skuId", skuId, "quantity", 2))));

        mvc.perform(jsonRequest(post(V1 + UrlConstant.Checkout.QUOTE_GUEST), req))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.summary.grandTotal").value(2_000_000));

        // Thiếu địa chỉ -> 400.
        Map<String, Object> bad = new java.util.HashMap<>(req);
        bad.remove("addressLine");
        mvc.perform(jsonRequest(post(V1 + UrlConstant.Order.GUEST), bad)).andExpect(status().isBadRequest());

        var placed = mvc.perform(jsonRequest(post(V1 + UrlConstant.Order.GUEST), req)
                        .header("X-Guest-Cart-Id", guestId == null ? "" : guestId))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.grandTotal").value(2_000_000));
        JsonNode order = body(placed);
        String code = order.get("code").asText();

        // Tra cứu: đúng SĐT (định dạng khác vẫn khớp) -> 200; sai SĐT hoặc thiếu -> 404/400.
        mvc.perform(get(V1 + UrlConstant.Order.GUEST + "/" + code).param("phone", "091 234 5678"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.code").value(code));
        mvc.perform(get(V1 + UrlConstant.Order.GUEST + "/" + code).param("phone", "0999999999"))
                .andExpect(status().isNotFound());
        mvc.perform(get(V1 + UrlConstant.Order.GUEST + "/" + code)).andExpect(status().isBadRequest());

        // Đơn đã giữ chỗ tồn (5 -> 3) và admin thấy được đơn.
        org.junit.jupiter.api.Assertions.assertEquals(3, body(mvc.perform(get(CATALOG + "/" + productId + "/variants")
                .header("Authorization", admin))).get(0).get("availableStock").asInt());
        mvc.perform(get(V1 + UrlAdminConstant.Order.BASE + "/" + code).header("Authorization", admin))
                .andExpect(status().isOk());
    }

    @Test
    void orderOfLoggedInCustomerIsNotReadableAsGuestOrder() throws Exception {
        // Mã đơn không tồn tại -> 404 (không lộ thông tin).
        mvc.perform(get(V1 + UrlConstant.Order.GUEST + "/KHONG-CO").param("phone", PHONE))
                .andExpect(status().isNotFound());
    }
}
