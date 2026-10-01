package com.pk.core.business.service.impl;

import lombok.RequiredArgsConstructor;

import com.pk.core.business.repository.CategoryRepo;
import com.pk.core.business.service.CategoryService;
import com.pk.core.common.exception.BusinessException;
import com.pk.core.common.exception.ErrorCode;
import com.pk.core.common.exception.ResourceNotFoundException;
import com.pk.core.common.util.SlugUtil;
import com.pk.core.model.dto.request.AdminCategoryRequestDto;
import com.pk.core.model.dto.response.CategoryResponseDto;
import com.pk.core.model.entity.CategoryEntity;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepo categories;

    @Transactional(readOnly = true)
    @Override
    public List<CategoryResponseDto> findAllActive() {
        return categories.findAllActive().stream().map(CategoryResponseDto::from).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<CategoryResponseDto> findAllForAdmin() {
        return categories.findAll(Sort.unsorted()).stream().map(CategoryResponseDto::from).toList();
    }

    @Transactional
    @Override
    public CategoryResponseDto create(AdminCategoryRequestDto req) {
        String slug = resolveSlug(req.getSlug(), req.getName());
        if (categories.findBySlug(slug) != null) {
            throw BusinessException.conflict(ErrorCode.DUPLICATE_SLUG, "Đường dẫn (slug) đã tồn tại: " + slug, slug);
        }
        CategoryEntity c = new CategoryEntity(req.getName().trim(), slug, req.getDescription());
        c.setImageUrl(req.getImageUrl());
        c.setParentId(req.getParentId());
        c.setSortOrder(req.getSortOrder());
        categories.create(c);
        return CategoryResponseDto.from(c);
    }

    @Transactional
    @Override
    public CategoryResponseDto update(Long categoryId, AdminCategoryRequestDto req) {
        CategoryEntity c = categories.findOne(categoryId);
        if (c == null) {
            throw new ResourceNotFoundException("Danh mục", "Category", categoryId);
        }
        String slug = resolveSlug(req.getSlug(), req.getName());
        if (!slug.equals(c.getSlug()) && categories.findBySlug(slug) != null) {
            throw BusinessException.conflict(ErrorCode.DUPLICATE_SLUG, "Đường dẫn (slug) đã tồn tại: " + slug, slug);
        }
        c.setName(req.getName().trim());
        c.setSlug(slug);
        c.setDescription(req.getDescription());
        c.setImageUrl(req.getImageUrl());
        c.setParentId(req.getParentId());
        c.setSortOrder(req.getSortOrder());
        c.touch();
        categories.update(c);
        return CategoryResponseDto.from(c);
    }

    private static String resolveSlug(String slug, String name) {
        return (slug == null || slug.isBlank()) ? SlugUtil.slugify(name) : SlugUtil.slugify(slug);
    }
}
