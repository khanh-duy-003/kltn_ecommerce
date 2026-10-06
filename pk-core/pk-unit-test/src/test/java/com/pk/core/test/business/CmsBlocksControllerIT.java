package com.pk.core.test.business;

import com.fasterxml.jackson.databind.JsonNode;
import com.pk.core.business.repository.BannerPlacementRepo;
import com.pk.core.business.repository.CategoryRepo;
import com.pk.core.business.repository.ProductRepo;
import com.pk.core.model.constant.UrlConstant;
import com.pk.core.model.constant.admin.UrlAdminConstant;
import com.pk.core.model.entity.BannerPlacementEntity;
import com.pk.core.model.entity.CategoryEntity;
import com.pk.core.model.entity.ProductEntity;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Block CMS theo spec (config object, targetSegment) và nội dung storefront đã nạp sẵn cho BANNER/PRODUCT_CAROUSEL. */
class CmsBlocksControllerIT extends IntegrationTestBase {

    @Autowired BannerPlacementRepo placements;
    @Autowired CategoryRepo categories;
    @Autowired ProductRepo products;

    private static String tag() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    private String admin() throws Exception {
        return bearer(adminAccessToken());
    }

    @Test
    void unknownBlockTypeAndBadConfigAreRejected() throws Exception {
        String slug = "trang-" + tag();
        mvc.perform(jsonRequest(post(UrlAdminConstant.Cms.PAGES), Map.of("name", "Trang", "slug", slug,
                        "status", "DRAFT", "blocks", List.of(Map.of("type", "HERO", "sortOrder", 0, "config", Map.of()))))
                        .header("Authorization", admin()))
                .andExpect(status().isBadRequest());
        mvc.perform(jsonRequest(post(UrlAdminConstant.Cms.PAGES), Map.of("name", "Trang", "slug", slug,
                        "status", "DRAFT", "blocks", List.of(Map.of("type", "BANNER", "sortOrder", 0, "config", Map.of()))))
                        .header("Authorization", admin()))
                .andExpect(status().isBadRequest());
        // Lỗi block không để lại trang dở dang: tạo lại cùng slug với block hợp lệ phải thành công (không 409).
        mvc.perform(jsonRequest(post(UrlAdminConstant.Cms.PAGES), Map.of("name", "Trang", "slug", slug,
                        "status", "DRAFT", "blocks", List.of(Map.of("type", "IMAGE_GALLERY", "sortOrder", 0,
                                "config", Map.of("images", List.of(Map.of("url", "/a.jpg")))))))
                        .header("Authorization", admin()))
                .andExpect(status().isCreated());
    }

    @Test
    void blockCrudUsesConfigAndTargetSegment() throws Exception {
        String slug = "trang-" + tag();
        long pageId = body(mvc.perform(jsonRequest(post(UrlAdminConstant.Cms.PAGES),
                        Map.of("name", "Trang", "slug", slug, "status", "DRAFT")).header("Authorization", admin()))
                .andExpect(status().isCreated())).get("id").asLong();

        String blocksPath = UrlAdminConstant.Cms.PAGES + "/" + pageId + "/blocks";
        JsonNode block = body(mvc.perform(jsonRequest(post(blocksPath), Map.of("type", "INFO_CARDS", "sortOrder", 1,
                        "config", Map.of("cards", List.of(Map.of("title", "A"))), "targetSegment", "VIP"))
                        .header("Authorization", admin()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.config.cards[0].title").value("A"))
                .andExpect(jsonPath("$.data.targetSegment").value("VIP")));
        String blockId = block.get("id").asText();

        mvc.perform(jsonRequest(put(blocksPath + "/" + blockId), Map.of("type", "IMAGE_GALLERY", "sortOrder", 2,
                        "config", Map.of("images", List.of())))
                        .header("Authorization", admin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.type").value("IMAGE_GALLERY"))
                .andExpect(jsonPath("$.data.sortOrder").value(2));

        mvc.perform(delete(blocksPath + "/" + blockId).header("Authorization", admin()))
                .andExpect(status().isNoContent());
    }

    @Test
    void storefrontPageResolvesBannerPlacementAndProductCarousel() throws Exception {
        String code = "CMS_" + tag().toUpperCase();
        BannerPlacementEntity placement = new BannerPlacementEntity();
        placement.setCode(code);
        placement.setName("Vị trí CMS");
        placement.setDisplayType("GRID");
        placements.create(placement);
        mvc.perform(jsonRequest(post(UrlAdminConstant.Banner.BANNERS), Map.of("internalName", "Banner " + code,
                        "placementCode", code, "status", "ACTIVE", "title", "Xin chào"))
                        .header("Authorization", admin()))
                .andExpect(status().isOk());

        String t = tag();
        CategoryEntity category = new CategoryEntity("DM " + t, "dm-" + t, null);
        categories.create(category);
        ProductEntity product = new ProductEntity(category.getId(), "P-" + t, "SP " + t, "sp-" + t);
        product.setBasePrice(BigDecimal.valueOf(1_000_000));
        product.publish();
        products.create(product);

        String slug = "trang-" + tag();
        mvc.perform(jsonRequest(post(UrlAdminConstant.Cms.PAGES), Map.of("name", "Trang chủ", "slug", slug,
                        "status", "PUBLISHED", "blocks", List.of(
                                Map.of("type", "BANNER", "sortOrder", 1, "config", Map.of("placementCode", code)),
                                Map.of("type", "PRODUCT_CAROUSEL", "sortOrder", 2, "config", Map.of(
                                        "header", Map.of("title", "Nổi bật"),
                                        "productIds", List.of(String.valueOf(product.getId()), "khong-ton-tai"))))))
                        .header("Authorization", admin()))
                .andExpect(status().isCreated());

        mvc.perform(get(UrlConstant.Cms.PAGES + "/" + slug))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.blocks[0].type").value("BANNER"))
                .andExpect(jsonPath("$.data.blocks[0].content.layout").value("GRID"))
                .andExpect(jsonPath("$.data.blocks[0].content.placement.code").value(code))
                .andExpect(jsonPath("$.data.blocks[0].content.banners.length()").value(1))
                .andExpect(jsonPath("$.data.blocks[1].type").value("PRODUCT_CAROUSEL"))
                .andExpect(jsonPath("$.data.blocks[1].content.header.title").value("Nổi bật"))
                .andExpect(jsonPath("$.data.blocks[1].content.products.length()").value(1))
                .andExpect(jsonPath("$.data.blocks[1].content.products[0].slug").value("sp-" + t));
    }
}
