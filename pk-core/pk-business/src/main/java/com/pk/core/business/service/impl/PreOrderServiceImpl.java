package com.pk.core.business.service.impl;

import lombok.RequiredArgsConstructor;

import com.pk.core.business.repository.PreOrderConfigRepo;
import com.pk.core.business.service.PreOrderService;
import com.pk.core.model.dto.request.PreOrderConfigRequestDto;
import com.pk.core.model.dto.response.PreOrderConfigResponseDto;
import com.pk.core.model.entity.PreOrderConfigEntity;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PreOrderServiceImpl implements PreOrderService {

    private final PreOrderConfigRepo configs;

    @Transactional(readOnly = true)
    @Override
    public List<PreOrderConfigResponseDto> list() {
        List<PreOrderConfigEntity> all = new ArrayList<>();
        for (PreOrderConfigEntity c : configs.findAll(Sort.unsorted())) {
            all.add(c);
        }
        all.sort(Comparator.comparing(PreOrderConfigEntity::getId).reversed());
        return all.stream().map(PreOrderConfigResponseDto::from).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public PreOrderConfigResponseDto current() {
        List<PreOrderConfigResponseDto> all = list();
        return all.isEmpty() ? new PreOrderConfigResponseDto(null, false, null, null) : all.get(0);
    }

    @Transactional(readOnly = true)
    @Override
    public boolean isEnabled() {
        return current().isEnabled();
    }

    @Transactional
    @Override
    public PreOrderConfigResponseDto create(Long adminId, PreOrderConfigRequestDto req) {
        String message = req.getMessage() == null || req.getMessage().isBlank() ? null : req.getMessage().trim();
        PreOrderConfigEntity entity = new PreOrderConfigEntity(Boolean.TRUE.equals(req.getEnabled()), message, adminId);
        configs.create(entity);
        return PreOrderConfigResponseDto.from(entity);
    }
}
