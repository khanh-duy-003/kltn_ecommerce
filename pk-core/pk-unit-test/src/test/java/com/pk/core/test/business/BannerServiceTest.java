package com.pk.core.test.business;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pk.core.business.repository.BannerPlacementRepo;
import com.pk.core.business.repository.BannerRepo;
import com.pk.core.business.service.impl.BannerServiceImpl;
import com.pk.core.common.exception.ResourceNotFoundException;
import com.pk.core.model.dto.request.AdminBannerRequestDto;
import com.pk.core.model.dto.request.BannerActionRequestDto;
import com.pk.core.model.dto.response.BannerPageResponseDto;
import com.pk.core.model.dto.response.BannerPlacementRenderResponseDto;
import com.pk.core.model.dto.response.BannerResponseDto;
import com.pk.core.model.entity.BannerEntity;
import com.pk.core.model.entity.BannerPlacementEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BannerServiceTest {

    @Mock BannerRepo banners;
    @Mock BannerPlacementRepo placements;

    private BannerServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new BannerServiceImpl(banners, placements, new ObjectMapper());
    }

    private static BannerEntity banner(long id, String name, String status, String mediaType, String actionsJson) {
        BannerEntity b = new BannerEntity();
        b.setId(id);
        b.setInternalName(name);
        b.setStatus(status);
        b.setMediaType(mediaType);
        b.setActions(actionsJson);
        return b;
    }

    private static BannerPlacementEntity placement(String code) {
        BannerPlacementEntity p = new BannerPlacementEntity();
        p.setCode(code);
        p.setName("Vị trí " + code);
        p.setDisplayType("CAROUSEL");
        return p;
    }

    // ------------------------------------------------------------------ create / update / delete

    @Test
    void createAppliesDefaultsSerializesActionsAndNormalizesPlacementCode() {
        when(placements.findByCode("HOME_HERO")).thenReturn(placement("HOME_HERO"));
        AdminBannerRequestDto req = new AdminBannerRequestDto();
        req.setInternalName("  Hero mùa thu ");
        req.setTitle("Thu đông");
        req.setOverlayOpacity(0.4);
        req.setPlacementCode("home_hero");
        req.setSortOrder(3);
        req.setActions(List.of(new BannerActionRequestDto("URL", "/khuyen-mai", "Mua ngay", null, null)));

        BannerResponseDto dto = service.create(req);

        ArgumentCaptor<BannerEntity> saved = ArgumentCaptor.forClass(BannerEntity.class);
        verify(banners).create(saved.capture());
        assertEquals("Hero mùa thu", saved.getValue().getInternalName());
        assertEquals("HOME_HERO", saved.getValue().getPlacementCode());
        assertEquals(3, saved.getValue().getSortOrder());
        assertTrue(saved.getValue().getActions().contains("\"actionType\":\"URL\""));
        assertEquals(BannerEntity.DRAFT, dto.getStatus());
        assertEquals("cover", dto.getMediaFit());
        assertEquals("IMAGE", dto.getMediaType());
        assertEquals(0.4, dto.getOverlayOpacity());
        assertEquals(1, dto.getActions().size());
        assertEquals("/khuyen-mai", dto.getActions().get(0).getActionTarget());
    }

    @Test
    void createWithUnknownPlacementIs404AndNothingSaved() {
        when(placements.findByCode("NOPE")).thenReturn(null);
        AdminBannerRequestDto req = new AdminBannerRequestDto();
        req.setInternalName("x");
        req.setPlacementCode("nope");

        assertThrows(ResourceNotFoundException.class, () -> service.create(req));
        verify(banners, never()).create(any(BannerEntity.class));
    }

    @Test
    void updateOnlyWritesNonNullFields() {
        BannerEntity existing = banner(1L, "Cũ", BannerEntity.DRAFT, "IMAGE", null);
        existing.setTitle("Tiêu đề cũ");
        when(banners.findOne(1L)).thenReturn(existing);
        AdminBannerRequestDto req = new AdminBannerRequestDto();
        req.setStatus("ACTIVE");

        BannerResponseDto dto = service.update(1L, req);

        assertEquals("ACTIVE", dto.getStatus());
        assertEquals("Tiêu đề cũ", dto.getTitle());
        assertEquals("Cũ", dto.getInternalName());
        assertNotNull(existing.getUpdatedDate());
        verify(banners).update(existing);
    }

    @Test
    void updateWithBlankPlacementCodeDetachesFromPlacement() {
        BannerEntity existing = banner(1L, "Cũ", BannerEntity.ACTIVE, "IMAGE", null);
        existing.setPlacementCode("HOME_HERO");
        when(banners.findOne(1L)).thenReturn(existing);
        AdminBannerRequestDto req = new AdminBannerRequestDto();
        req.setPlacementCode("");

        BannerResponseDto dto = service.update(1L, req);

        assertNull(dto.getPlacementCode());
    }

    @Test
    void missingOrSoftDeletedBannerIs404() {
        when(banners.findOne(1L)).thenReturn(null);
        BannerEntity deleted = banner(2L, "Đã xoá", BannerEntity.ACTIVE, "IMAGE", null);
        deleted.setDeletedDate(new Date());
        when(banners.findOne(2L)).thenReturn(deleted);

        assertThrows(ResourceNotFoundException.class, () -> service.findById(1L));
        assertThrows(ResourceNotFoundException.class, () -> service.findById(2L));
        assertThrows(ResourceNotFoundException.class, () -> service.delete(2L));
        assertThrows(ResourceNotFoundException.class, () -> service.update(1L, new AdminBannerRequestDto()));
    }

    @Test
    void deleteIsSoft() {
        BannerEntity existing = banner(1L, "Cũ", BannerEntity.ACTIVE, "IMAGE", null);
        when(banners.findOne(1L)).thenReturn(existing);

        service.delete(1L);

        assertNotNull(existing.getDeletedDate());
        verify(banners).update(existing);
    }

    // ------------------------------------------------------------------ list

    private void stubThreeBanners() {
        BannerEntity a = banner(1L, "Hero One", "ACTIVE", "IMAGE", "[{\"actionType\":\"URL\",\"actionTarget\":\"/a\"}]");
        BannerEntity b = banner(2L, "Promo", "DRAFT", "VIDEO", null);
        BannerEntity c = banner(3L, "Hero Deleted", "ACTIVE", "IMAGE", "[{\"actionType\":\"PRODUCT\",\"actionTarget\":\"7\"}]");
        c.setDeletedDate(new Date());
        when(banners.findAll(any(Sort.class))).thenReturn(List.of(a, b, c));
    }

    @Test
    void listWithoutFiltersSkipsDeletedAndPutsNewestFirst() {
        stubThreeBanners();

        BannerPageResponseDto page = service.findAll(1, 20, null, null, null, null);

        assertEquals(2, page.getTotal());
        assertEquals("2", page.getItems().get(0).getId());
        assertEquals("1", page.getItems().get(1).getId());
        assertEquals(1, page.getPage());
        assertEquals(20, page.getTake());
    }

    @Test
    void listFilters() {
        stubThreeBanners();

        assertEquals(1, service.findAll(1, 20, "active", null, null, null).getTotal());
        assertEquals(1, service.findAll(1, 20, null, "video", null, null).getTotal());
        assertEquals("1", service.findAll(1, 20, null, null, "LINK", null).getItems().get(0).getId());
        assertEquals(0, service.findAll(1, 20, null, null, "PRODUCT", null).getTotal()); // banner PRODUCT đã xoá mềm
        assertEquals("1", service.findAll(1, 20, null, null, null, "HERO").getItems().get(0).getId());
        assertEquals(0, service.findAll(1, 20, "ACTIVE", "VIDEO", null, null).getTotal());
    }

    @Test
    void listPaginates() {
        stubThreeBanners();

        BannerPageResponseDto page2 = service.findAll(2, 1, null, null, null, null);

        assertEquals(2, page2.getTotal());
        assertEquals(1, page2.getItems().size());
        assertEquals("1", page2.getItems().get(0).getId());
    }

    // ------------------------------------------------------------------ render

    @Test
    void renderReturnsActiveBannersOfPlacement() {
        when(placements.findByCode("HOME_HERO")).thenReturn(placement("HOME_HERO"));
        when(banners.findActiveByPlacementCode("HOME_HERO"))
                .thenReturn(List.of(banner(1L, "A", "ACTIVE", "IMAGE", null)));

        BannerPlacementRenderResponseDto res = service.renderByPlacementCode(" home_hero ");

        assertEquals("HOME_HERO", res.getCode());
        assertEquals("CAROUSEL", res.getDisplayType());
        assertEquals(1, res.getBanners().size());
    }

    @Test
    void renderUnknownPlacementIs404() {
        when(placements.findByCode("NOPE")).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> service.renderByPlacementCode("nope"));
    }
}
