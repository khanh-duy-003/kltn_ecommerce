package com.pk.core.business.service.impl;

import com.pk.core.business.repository.BannerPlacementRepo;
import com.pk.core.business.service.BannerPlacementService;
import com.pk.core.common.exception.BusinessException;
import com.pk.core.common.exception.ErrorCode;
import com.pk.core.common.exception.ResourceNotFoundException;
import com.pk.core.model.dto.request.AdminBannerPlacementRequestDto;
import com.pk.core.model.dto.response.BannerPlacementResponseDto;
import com.pk.core.model.entity.BannerPlacementEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class BannerPlacementServiceImpl implements BannerPlacementService {

    private static final Set<String> DISPLAY_TYPES = Set.of("CAROUSEL", "GRID", "SINGLE");
    private static final Pattern CODE = Pattern.compile("[A-Z0-9_]{1,60}");

    private final BannerPlacementRepo placements;

    @Transactional(readOnly = true)
    @Override
    public List<BannerPlacementResponseDto> findAll() {
        return placements.findAll(Sort.unsorted()).stream()
                .sorted(Comparator.comparing(BannerPlacementEntity::getId))
                .map(BannerPlacementResponseDto::from).toList();
    }

    @Transactional
    @Override
    public BannerPlacementResponseDto create(AdminBannerPlacementRequestDto req) {
        String code = req.getCode() == null ? "" : req.getCode().trim().toUpperCase(Locale.ROOT);
        if (!CODE.matcher(code).matches()) {
            throw BusinessException.badRequest(ErrorCode.VALIDATION_FAILED,
                    "code chỉ gồm chữ, số, gạch dưới (tối đa 60 ký tự), ví dụ HOME_HERO", req.getCode());
        }
        if (req.getName() == null || req.getName().isBlank()) {
            throw BusinessException.badRequest(ErrorCode.VALIDATION_FAILED, "name không được để trống");
        }
        if (placements.findByCode(code) != null) {
            throw BusinessException.conflict(ErrorCode.DUPLICATE_CODE, "Mã vị trí banner đã tồn tại: " + code, code);
        }
        BannerPlacementEntity e = new BannerPlacementEntity();
        e.setCode(code);
        e.setName(req.getName().trim());
        e.setDisplayType(normalizeType(req.getDisplayType(), "CAROUSEL"));
        placements.create(e);
        return BannerPlacementResponseDto.from(e);
    }

    @Transactional
    @Override
    public BannerPlacementResponseDto update(Long id, AdminBannerPlacementRequestDto req) {
        BannerPlacementEntity e = id == null ? null : placements.findOne(id);
        if (e == null) {
            throw new ResourceNotFoundException("Vị trí banner", "BannerPlacement", id);
        }
        if (req.getName() != null) {
            if (req.getName().isBlank()) {
                throw BusinessException.badRequest(ErrorCode.VALIDATION_FAILED, "name không được để trống");
            }
            e.setName(req.getName().trim());
        }
        if (req.getDisplayType() != null) {
            e.setDisplayType(normalizeType(req.getDisplayType(), e.getDisplayType()));
        }
        placements.update(e);
        return BannerPlacementResponseDto.from(e);
    }

    private static String normalizeType(String type, String fallback) {
        if (type == null || type.isBlank()) {
            return fallback;
        }
        String t = type.trim().toUpperCase(Locale.ROOT);
        if (!DISPLAY_TYPES.contains(t)) {
            throw BusinessException.badRequest(ErrorCode.VALIDATION_FAILED,
                    "displayType không hợp lệ: " + type + " (CAROUSEL | GRID | SINGLE)", type);
        }
        return t;
    }
}
