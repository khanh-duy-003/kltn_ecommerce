package com.pk.core.business.service;

import com.pk.core.model.dto.request.AdminBannerPlacementRequestDto;
import com.pk.core.model.dto.response.BannerPlacementResponseDto;

import java.util.List;

public interface BannerPlacementService {

    List<BannerPlacementResponseDto> findAll();

    BannerPlacementResponseDto create(AdminBannerPlacementRequestDto req);

    BannerPlacementResponseDto update(Long id, AdminBannerPlacementRequestDto req);
}
