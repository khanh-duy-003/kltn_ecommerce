package com.pk.core.test.business;

import com.fasterxml.jackson.databind.JsonNode;
import com.pk.core.business.repository.CategoryRepo;
import com.pk.core.business.repository.ProductRepo;
import com.pk.core.business.repository.ProductSkuRepo;
import com.pk.core.model.constant.UrlConstant;
import com.pk.core.model.constant.admin.UrlAdminConstant;
import com.pk.core.model.entity.CategoryEntity;
import com.pk.core.model.entity.ProductEntity;
import com.pk.core.model.entity.ProductSkuEntity;
import com.pk.core.test.common.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Yêu cầu khách hàng: khách gửi công khai (3 loại) + admin xem/đổi trạng thái. */
class CustomerRequestControllerIT extends IntegrationTestBase {

    private static final String PUBLIC = UrlConstant.Common.API + UrlConstant.Common.VERSION + UrlConstant.CustomerRequest.BASE;
    private static final String ADMIN = UrlConstant.Common.API + UrlConstant.Common.VERSION + UrlAdminConstant.CustomerRequest.BASE;

    @Autowired CategoryRepo categories;
    @Autowired ProductRepo products;
    @Autowired ProductSkuRepo productSkus;

    private static String tag() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    private String newSkuCode() {
        String tag = tag();
        CategoryEntity category = new CategoryEntity("DM " + tag, "dm-" + tag, null);
        categories.create(category);
        ProductEntity product = new ProductEntity(category.getId(), "P-" + tag, "Nhẫn " + tag, "nhan-" + tag);
        product.setThumbnailUrl("https://img/" + tag + ".jpg");
        product.publish();
        products.create(product);
        ProductSkuEntity sku = new ProductSkuEntity("SKU-" + tag, "M", BigDecimal.valueOf(900_000), 0);
        sku.setProductId(product.getId());
        sku.setName("Nhẫn " + tag);
        sku.setStatus(ProductSkuEntity.PUBLISHED);
        productSkus.create(sku);
        return sku.getSkuCode();
    }

    @Test
    void publicEndpointsValidateBody() throws Exception {
        postJson(PUBLIC + "/back-in-stock", Map.of("phone", "abc", "skuCode", "X"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field=='phone')]").exists());
        postJson(PUBLIC + "/newsletter", Map.of("email", "khong-phai-email"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field=='email')]").exists());
        postJson(PUBLIC + "/order-support", Map.of("phone", uniquePhone()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void backInStockUnknownSkuIs404() throws Exception {
        postJson(PUBLIC + "/back-in-stock", Map.of("phone", uniquePhone(), "skuCode", "KHONG-CO-" + tag()))
                .andExpect(status().isNotFound());
    }

    @Test
    void backInStockIsPublicIdempotentAndVisibleToAdmin() throws Exception {
        String skuCode = newSkuCode();
        String phone = uniquePhone();
        Map<String, Object> body = Map.of("phone", phone, "skuCode", skuCode);

        postJson(PUBLIC + "/back-in-stock", body)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.success").value(true));
        postJson(PUBLIC + "/back-in-stock", body).andExpect(status().isOk()); // gửi trùng: vẫn 200, không tạo thêm

        String admin = bearer(adminAccessToken());
        JsonNode list = body(mvc.perform(get(ADMIN + "/back-in-stock").param("keyword", phone)
                        .header("Authorization", admin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.items[0].type").value("BACK_IN_STOCK"))
                .andExpect(jsonPath("$.data.items[0].contactChannel").value("PHONE"))
                .andExpect(jsonPath("$.data.items[0].contactValue").value(phone))
                .andExpect(jsonPath("$.data.items[0].skuCode").value(skuCode))
                .andExpect(jsonPath("$.data.items[0].status").value("NEW")));
        String id = list.get("items").get(0).get("id").asText();

        mvc.perform(get(ADMIN + "/" + id).header("Authorization", admin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(id))
                .andExpect(jsonPath("$.data.variantTextSnapshot").value("M"));

        mvc.perform(jsonRequest(patch(ADMIN + "/" + id + "/status"), Map.of("status", "CONTACTED"))
                        .header("Authorization", admin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.success").value(true));
        mvc.perform(get(ADMIN + "/back-in-stock").param("keyword", phone).param("status", "CONTACTED")
                        .header("Authorization", admin))
                .andExpect(jsonPath("$.data.total").value(1));
        mvc.perform(get(ADMIN + "/back-in-stock").param("keyword", phone).param("status", "NEW")
                        .header("Authorization", admin))
                .andExpect(jsonPath("$.data.total").value(0));
    }

    @Test
    void orderSupportAndNewsletterAndBulkStatus() throws Exception {
        String phone = uniquePhone();
        String email = "news-" + tag() + "@test.local";
        postJson(PUBLIC + "/order-support", Map.of("phone", phone, "orderCode", "KHONG-CO-" + tag()))
                .andExpect(status().isOk());
        postJson(PUBLIC + "/newsletter", Map.of("email", email.toUpperCase())).andExpect(status().isOk());

        String admin = bearer(adminAccessToken());
        mvc.perform(get(ADMIN + "/order-support").param("keyword", phone).header("Authorization", admin))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.items[0].orderType").value("ORDER"));
        JsonNode news = body(mvc.perform(get(ADMIN + "/newsletter").param("keyword", email)
                        .header("Authorization", admin))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.items[0].contactChannel").value("EMAIL"))
                .andExpect(jsonPath("$.data.items[0].contactValue").value(email)));
        String newsId = news.get("items").get(0).get("id").asText();

        mvc.perform(jsonRequest(patch(ADMIN + "/status/bulk"),
                        Map.of("status", "COMPLETED", "ids", List.of(newsId), "type", "NEWSLETTER"))
                        .header("Authorization", admin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.success").value(true));
        mvc.perform(get(ADMIN + "/newsletter").param("keyword", email).param("status", "COMPLETED")
                        .header("Authorization", admin))
                .andExpect(jsonPath("$.data.total").value(1));
    }

    @Test
    void adminEndpointsRequireAdminRoleAndValidateInput() throws Exception {
        mvc.perform(get(ADMIN + "/newsletter")).andExpect(status().isUnauthorized());
        mvc.perform(get(ADMIN + "/newsletter").header("Authorization", bearer(customerAccessToken())))
                .andExpect(status().isForbidden());

        String admin = bearer(adminAccessToken());
        mvc.perform(get(ADMIN + "/newsletter").param("status", "WHATEVER").header("Authorization", admin))
                .andExpect(status().isBadRequest());
        mvc.perform(get(ADMIN + "/99999999").header("Authorization", admin)).andExpect(status().isNotFound());
        mvc.perform(jsonRequest(patch(ADMIN + "/1/status"), Map.of("status", "NOPE")).header("Authorization", admin))
                .andExpect(status().isBadRequest());
    }
}
