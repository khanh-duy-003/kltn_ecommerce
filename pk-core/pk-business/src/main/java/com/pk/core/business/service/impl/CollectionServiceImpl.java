package com.pk.core.business.service.impl;

import lombok.RequiredArgsConstructor;

import com.pk.core.business.repository.CollectionRepo;
import com.pk.core.business.service.CollectionService;
import com.pk.core.common.exception.BusinessException;
import com.pk.core.common.exception.ErrorCode;
import com.pk.core.common.exception.ResourceNotFoundException;
import com.pk.core.common.util.SlugUtil;
import com.pk.core.model.dto.request.AdminCollectionRequestDto;
import com.pk.core.model.dto.response.CollectionResponseDto;
import com.pk.core.model.entity.CollectionEntity;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CollectionServiceImpl implements CollectionService {

    private final CollectionRepo collections;

    @Transactional(readOnly = true)
    @Override
    public List<CollectionResponseDto> findAllPublished() {
        return collections.findAllPublished().stream().map(CollectionResponseDto::from).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<CollectionResponseDto> findAllForAdmin() {
        return collections.findAll(Sort.unsorted()).stream().map(CollectionResponseDto::from).toList();
    }

    @Transactional
    @Override
    public CollectionResponseDto create(AdminCollectionRequestDto req) {
        String slug = resolveSlug(req.getSlug(), req.getName());
        if (collections.findBySlug(slug) != null) {
            throw BusinessException.conflict(ErrorCode.DUPLICATE_SLUG, "Đường dẫn (slug) đã tồn tại: " + slug, slug);
        }
        CollectionEntity c = new CollectionEntity(req.getName().trim(), slug, req.getDescription());
        c.setHeroImageUrl(req.getHeroImageUrl());
        if (req.getStatus() != null && !req.getStatus().isBlank()) {
            c.setStatus(req.getStatus().trim().toUpperCase());
        }
        collections.create(c);
        return CollectionResponseDto.from(c);
    }

    @Transactional
    @Override
    public CollectionResponseDto update(Long collectionId, AdminCollectionRequestDto req) {
        CollectionEntity c = collections.findOne(collectionId);
        if (c == null) {
            throw new ResourceNotFoundException("Bộ sưu tập", "Collection", collectionId);
        }
        String slug = resolveSlug(req.getSlug(), req.getName());
        if (!slug.equals(c.getSlug()) && collections.findBySlug(slug) != null) {
            throw BusinessException.conflict(ErrorCode.DUPLICATE_SLUG, "Đường dẫn (slug) đã tồn tại: " + slug, slug);
        }
        c.setName(req.getName().trim());
        c.setSlug(slug);
        c.setDescription(req.getDescription());
        c.setHeroImageUrl(req.getHeroImageUrl());
        if (req.getStatus() != null && !req.getStatus().isBlank()) {
            c.setStatus(req.getStatus().trim().toUpperCase());
        }
        c.touch();
        collections.update(c);
        return CollectionResponseDto.from(c);
    }

    private static String resolveSlug(String slug, String name) {
        return (slug == null || slug.isBlank()) ? SlugUtil.slugify(name) : SlugUtil.slugify(slug);
    }
}
