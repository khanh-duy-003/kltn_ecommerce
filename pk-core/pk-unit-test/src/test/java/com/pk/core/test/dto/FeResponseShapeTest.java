package com.pk.core.test.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pk.core.model.dto.response.AddressResponseDto;
import com.pk.core.model.dto.response.BannerPlacementRenderResponseDto;
import com.pk.core.model.dto.response.BannerResponseDto;
import com.pk.core.model.dto.response.CategoryResponseDto;
import com.pk.core.model.dto.response.CmsStorefrontBlockResponseDto;
import com.pk.core.model.dto.response.CmsStorefrontPageResponseDto;
import com.pk.core.model.dto.response.OrderItemResponseDto;
import com.pk.core.model.dto.response.UserResponseDto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Kiểm tra JSON trả ra có thêm các field theo kiểu FE (src FE) mà vẫn giữ field cũ. Không cần Spring. */
class FeResponseShapeTest {

    private final ObjectMapper mapper = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);

    private JsonNode json(Object o) {
        return mapper.valueToTree(o);
    }

    @Test
    void cmsStorefrontPageHasPageLayoutAndBlockTypeCode() {
        CmsStorefrontBlockResponseDto block = new CmsStorefrontBlockResponseDto("5", "BANNER", 1, Map.of(), null);
        JsonNode n = json(new CmsStorefrontPageResponseDto("9", "Trang chủ", "trang-chu", "PUBLISHED", List.of(block)));
        assertEquals("trang-chu", n.get("slug").asText()); // field cũ còn
        assertEquals("9", n.get("page").get("id").asText());
        assertEquals("Trang chủ", n.get("page").get("seo").get("title").asText());
        assertEquals("vi", n.get("page").get("locale").asText());
        assertTrue(n.get("layout").has("versionId"));
        assertEquals("BANNER", n.get("blocks").get(0).get("blockTypeCode").asText());
        assertEquals("BANNER", n.get("blocks").get(0).get("type").asText());
    }

    @Test
    void bannerPlacementHasSlotsAndSnakeCaseBannerFields() {
        BannerResponseDto b = new BannerResponseDto();
        b.setId("1");
        b.setInternalName("Banner A");
        b.setMediaUrl("/a.jpg");
        b.setMediaType("IMAGE");
        b.setTitleColor("#fff");
        BannerPlacementRenderResponseDto p = new BannerPlacementRenderResponseDto("HOME", "Home", "GRID", List.of(b, b));
        JsonNode n = json(p);
        assertEquals("HOME", n.get("code").asText());
        assertEquals("GRID", n.get("type").asText());
        JsonNode slot = n.get("slots").get(0);
        assertEquals("main", slot.get("slot_key").asText());
        assertEquals("MULTIPLE_IMAGE", slot.get("slot_type").asText());
        assertEquals(2, slot.get("banners").size());
        JsonNode banner = slot.get("banners").get(1).get("banner");
        assertEquals("Banner A", banner.get("internal_name").asText());
        assertEquals("/a.jpg", banner.get("media_url").asText());
        assertEquals("/a.jpg", banner.get("media_mobile_url").asText()); // rơi về media_url
        assertEquals("#fff", banner.get("title_color").asText());
        assertEquals("Banner A", banner.get("internalName").asText()); // field cũ còn
    }

    @Test
    void addressSplitsNameAndExposesFeFields() {
        JsonNode n = json(new AddressResponseDto(7L, "Nguyễn Văn An", "0912345678", "Hà Nội", "Cầu Giấy", "Dịch Vọng", "1 Xuân Thủy", true));
        assertEquals("An", n.get("firstName").asText());
        assertEquals("Nguyễn Văn", n.get("lastName").asText());
        assertEquals("0912345678", n.get("receiverPhone").asText());
        assertEquals("Dịch Vọng", n.get("wardName").asText());
        assertEquals("Hà Nội", n.get("provinceName").asText());
        assertTrue(n.get("isDefault").asBoolean());
        assertEquals(7, n.get("id").asLong()); // id vẫn là number
    }

    @Test
    void addressSingleWordNameHasEmptyLastName() {
        JsonNode n = json(new AddressResponseDto(1L, "An", "0912345678", "HN", "CG", "DV", "x", false));
        assertEquals("An", n.get("firstName").asText());
        assertEquals("", n.get("lastName").asText());
    }

    @Test
    void userHasFirstLastNameStatusAndType() {
        JsonNode n = json(new UserResponseDto(3L, "a@b.c", "Trần Thị Bích", "0900000000", List.of("CUSTOMER")));
        assertEquals("Bích", n.get("firstName").asText());
        assertEquals("Trần Thị", n.get("lastName").asText());
        assertEquals("ACTIVE", n.get("status").asText());
        assertEquals("CUSTOMER", n.get("type").asText());
        assertEquals("Trần Thị Bích", n.get("fullName").asText());
    }

    @Test
    void orderItemExposesFeNamesAndStringMoney() {
        JsonNode n = json(new OrderItemResponseDto(1L, 2L, 3L, "Nhẫn", "/n.jpg", 2,
                BigDecimal.valueOf(1_500_000), BigDecimal.valueOf(3_000_000)));
        assertEquals("3", n.get("variationId").asText());
        assertEquals("Nhẫn", n.get("productName").asText());
        assertEquals("/n.jpg", n.get("image").asText());
        assertEquals("1500000", n.get("salePrice").asText());
        assertEquals("3000000", n.get("finalAmount").asText());
        assertTrue(n.get("unitPrice").isNumber()); // field cũ giữ kiểu number
    }

    @Test
    void categoryHasCodeImageAndEmptyAncestorLists() {
        JsonNode n = json(new CategoryResponseDto(1L, null, "Nhẫn", "nhan", "mô tả", "/c.jpg", 0));
        assertEquals("nhan", n.get("code").asText());
        assertEquals("/c.jpg", n.get("image").asText());
        assertEquals(0, n.get("ancestorCategorySlugs").size());
        assertEquals("nhan", n.get("slug").asText());
    }
}
