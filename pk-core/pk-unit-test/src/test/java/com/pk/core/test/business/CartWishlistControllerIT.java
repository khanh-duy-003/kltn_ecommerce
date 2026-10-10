package com.pk.core.test.business;

import com.fasterxml.jackson.databind.JsonNode;
import com.pk.core.business.repository.CategoryRepo;
import com.pk.core.business.repository.ProductRepo;
import com.pk.core.business.repository.ProductSkuRepo;
import com.pk.core.model.constant.UrlConstant;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Giỏ hàng (công khai + header X-Guest-Cart-Id, /merge cần đăng nhập) và Wishlist (cần đăng nhập). Mỗi test tự
 * tạo danh mục/sản phẩm/SKU riêng (mã ngẫu nhiên) vì DB test dùng chung.
 */
class CartWishlistControllerIT extends IntegrationTestBase {

    private static final String GUEST_HEADER = "X-Guest-Cart-Id";

    @Autowired CategoryRepo categories;
    @Autowired ProductRepo products;
    @Autowired ProductSkuRepo productSkus;

    /** Tạo 1 sản phẩm PUBLISHED có 1 SKU PUBLISHED; trả [productId, skuId]. */
    private long[] newPublishedProduct(int onHand, long price) {
        String tag = UUID.randomUUID().toString().substring(0, 8);
        CategoryEntity category = new CategoryEntity("Danh mục " + tag, "danh-muc-" + tag, null);
        categories.create(category);

        ProductEntity product = new ProductEntity(category.getId(), "P-" + tag, "Sản phẩm " + tag, "san-pham-" + tag);
        product.setBasePrice(BigDecimal.valueOf(price));
        product.publish();
        products.create(product);

        ProductSkuEntity sku = new ProductSkuEntity("SKU-" + tag, "M", BigDecimal.valueOf(price), onHand);
        sku.setProductId(product.getId());
        sku.setName("Nhẫn " + tag);
        sku.setStatus(ProductSkuEntity.PUBLISHED);
        sku.setDefault(true);
        productSkus.create(sku);
        return new long[] {product.getId(), sku.getId()};
    }

    private static Map<String, Object> addBody(long skuId, int qty) {
        return Map.of("clearAll", false, "items", List.of(Map.of("variationId", String.valueOf(skuId), "quantity", qty)));
    }

    // ------------------------------------------------------------------ cart

    @Test
    void guestCartFlowAddAccumulateAndRead() throws Exception {
        long skuId = newPublishedProduct(10, 1_000_000)[1];

        JsonNode first = body(postJson(UrlConstant.Common.API + UrlConstant.Common.VERSION + UrlConstant.Cart.BASE, addBody(skuId, 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.success").value(true)));
        String guestId = first.get("guestId").asText();

        mvc.perform(jsonRequest(post(UrlConstant.Common.API + UrlConstant.Common.VERSION + UrlConstant.Cart.BASE), addBody(skuId, 3)).header(GUEST_HEADER, guestId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.items.length()").value(1))
                .andExpect(jsonPath("$.data.items[0].quantity").value(5));

        mvc.perform(get(UrlConstant.Common.API + UrlConstant.Common.VERSION + UrlConstant.Cart.BASE).header(GUEST_HEADER, guestId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.items[0].quantity").value(5))
                .andExpect(jsonPath("$.data.items[0].variationId").value(String.valueOf(skuId)))
                .andExpect(jsonPath("$.data.items[0].sellingPriceAfterTaxMinor").value(1_000_000));
    }

    @Test
    void quantityBeyondStockIsCappedAndReported() throws Exception {
        long skuId = newPublishedProduct(3, 500_000)[1];

        postJson(UrlConstant.Common.API + UrlConstant.Common.VERSION + UrlConstant.Cart.BASE, addBody(skuId, 9))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.success").value(false))
                .andExpect(jsonPath("$.data.reason").value("INSUFFICIENT_STOCK"))
                .andExpect(jsonPath("$.data.items[0].quantity").value(3));
    }

    @Test
    void invalidVariationIdReturns400() throws Exception {
        postJson(UrlConstant.Common.API + UrlConstant.Common.VERSION + UrlConstant.Cart.BASE, Map.of("clearAll", false,
                "items", List.of(Map.of("variationId", "abc", "quantity", 1))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].code").value("CART_ITEM_INVALID"));
    }

    @Test
    void mergeRequiresLoginThenMovesGuestCartIntoAccount() throws Exception {
        long skuId = newPublishedProduct(10, 1_000_000)[1];
        String guestId = body(postJson(UrlConstant.Common.API + UrlConstant.Common.VERSION + UrlConstant.Cart.BASE, addBody(skuId, 2))).get("guestId").asText();

        mvc.perform(post(UrlConstant.Common.API + UrlConstant.Common.VERSION + UrlConstant.Cart.BASE + "/merge").header(GUEST_HEADER, guestId))
                .andExpect(status().isUnauthorized());

        String token = customerAccessToken();
        mvc.perform(post(UrlConstant.Common.API + UrlConstant.Common.VERSION + UrlConstant.Cart.BASE + "/merge")
                        .header(GUEST_HEADER, guestId).header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.items.length()").value(1))
                .andExpect(jsonPath("$.data.items[0].quantity").value(2));

        // Giỏ vãng lai đã bị xoá, giỏ tài khoản có hàng.
        mvc.perform(get(UrlConstant.Common.API + UrlConstant.Common.VERSION + UrlConstant.Cart.BASE).header(GUEST_HEADER, guestId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.items.length()").value(0));
        mvc.perform(get(UrlConstant.Common.API + UrlConstant.Common.VERSION + UrlConstant.Cart.BASE).header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.items[0].quantity").value(2));
    }

    @Test
    void calculateTotalIsPublic() throws Exception {
        long skuId = newPublishedProduct(10, 1_000_000)[1];

        postJson(UrlConstant.Common.API + UrlConstant.Common.VERSION + UrlConstant.Cart.BASE + "/calculate-total",
                Map.of("items", List.of(Map.of("variationId", String.valueOf(skuId), "quantity", 3))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.subTotal").value(3_000_000))
                .andExpect(jsonPath("$.data.totalAmount").value(3_000_000));
    }

    @Test
    void recommendationIsPublicAndPaged() throws Exception {
        newPublishedProduct(10, 700_000);

        mvc.perform(get(UrlConstant.Common.API + UrlConstant.Common.VERSION + UrlConstant.Cart.BASE + "/recommendation").param("page", "1").param("take", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.pagination.currentPage").value(1))
                .andExpect(jsonPath("$.data.list").isArray());
    }

    // ------------------------------------------------------------------ wishlist

    @Test
    void wishlistRequiresLogin() throws Exception {
        mvc.perform(get(UrlConstant.Common.API + UrlConstant.Common.VERSION + UrlConstant.Wishlist.BASE)).andExpect(status().isUnauthorized());
        mvc.perform(get(UrlConstant.Common.API + UrlConstant.Common.VERSION + UrlConstant.Wishlist.BASE + "/product-ids")).andExpect(status().isUnauthorized());
    }

    @Test
    void wishlistAddStatusListAndRemove() throws Exception {
        long productId = newPublishedProduct(10, 1_000_000)[0];
        String auth = bearer(customerAccessToken());
        String pid = String.valueOf(productId);

        mvc.perform(jsonRequest(post(UrlConstant.Common.API + UrlConstant.Common.VERSION + UrlConstant.Wishlist.BASE), Map.of("productIds", List.of(pid)))
                        .header("Authorization", auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.productIds[0]").value(pid));
        // Thêm lần nữa vẫn chỉ 1 dòng (idempotent).
        mvc.perform(jsonRequest(post(UrlConstant.Common.API + UrlConstant.Common.VERSION + UrlConstant.Wishlist.BASE), Map.of("productIds", List.of(pid)))
                        .header("Authorization", auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.productIds.length()").value(1));

        mvc.perform(get(UrlConstant.Common.API + UrlConstant.Common.VERSION + UrlConstant.Wishlist.BASE + "/" + pid).header("Authorization", auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.wishlisted").value(true));
        mvc.perform(get(UrlConstant.Common.API + UrlConstant.Common.VERSION + UrlConstant.Wishlist.BASE + "/product-ids").header("Authorization", auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.productIds[0]").value(pid));
        mvc.perform(get(UrlConstant.Common.API + UrlConstant.Common.VERSION + UrlConstant.Wishlist.BASE).header("Authorization", auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.list[0].productId").value(pid))
                .andExpect(jsonPath("$.data.list[0].basePrice").value(1_000_000));

        mvc.perform(jsonRequest(delete(UrlConstant.Common.API + UrlConstant.Common.VERSION + UrlConstant.Wishlist.BASE), Map.of("productIds", List.of(pid)))
                        .header("Authorization", auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.productIds.length()").value(0));
        mvc.perform(get(UrlConstant.Common.API + UrlConstant.Common.VERSION + UrlConstant.Wishlist.BASE + "/" + pid).header("Authorization", auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.wishlisted").value(false));
    }

    @Test
    void wishlistAddUnknownProductReturns404() throws Exception {
        mvc.perform(jsonRequest(post(UrlConstant.Common.API + UrlConstant.Common.VERSION + UrlConstant.Wishlist.BASE), Map.of("productIds", List.of("999999999")))
                        .header("Authorization", bearer(customerAccessToken())))
                .andExpect(status().isNotFound());
    }

    @Test
    void wishlistAddNonNumericProductIdReturns400() throws Exception {
        mvc.perform(jsonRequest(post(UrlConstant.Common.API + UrlConstant.Common.VERSION + UrlConstant.Wishlist.BASE), Map.of("productIds", List.of("abc")))
                        .header("Authorization", bearer(customerAccessToken())))
                .andExpect(status().isBadRequest());
    }
}
