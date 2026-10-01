package com.pk.core.business.service.impl;

import lombok.RequiredArgsConstructor;

import com.pk.core.business.repository.CmsBlockRepo;
import com.pk.core.business.repository.CmsPageRepo;
import com.pk.core.business.service.CmsService;
import com.pk.core.common.exception.BusinessException;
import com.pk.core.common.exception.ErrorCode;
import com.pk.core.common.exception.ResourceNotFoundException;
import com.pk.core.common.util.SlugUtil;
import com.pk.core.model.dto.request.AdminCmsBlockRequestDto;
import com.pk.core.model.dto.request.AdminCmsPageRequestDto;
import com.pk.core.model.dto.response.CmsBlockResponseDto;
import com.pk.core.model.dto.response.CmsPageResponseDto;
import com.pk.core.model.entity.CmsBlockEntity;
import com.pk.core.model.entity.CmsPageEntity;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CmsServiceImpl implements CmsService {

    private final CmsPageRepo pages;
    private final CmsBlockRepo blocks;

    @Transactional(readOnly = true)
    @Override
    public List<CmsPageResponseDto> findAllPages() {
        // findAll() KHÔNG tự lọc soft-delete (Mirage/PkRepo không biết deleted_date) - lọc tay bằng
        // Java, cùng cách PromotionServiceImpl.findAll() đã làm.
        return pages.findAll(Sort.unsorted()).stream()
                .filter(p -> p.getDeletedDate() == null)
                .map(p -> CmsPageResponseDto.from(p, toBlockDtos(p.getId())))
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public CmsPageResponseDto findPageById(Long pageId) {
        CmsPageEntity page = findPageOrThrow(pageId);
        return CmsPageResponseDto.from(page, toBlockDtos(page.getId()));
    }

    @Transactional
    @Override
    public CmsPageResponseDto createPage(AdminCmsPageRequestDto req) {
        String slug = resolveSlug(req.getSlug(), req.getTitle());
        if (pages.findBySlug(slug) != null) {
            throw BusinessException.conflict(ErrorCode.DUPLICATE_SLUG, "Đường dẫn (slug) đã tồn tại: " + slug, slug);
        }
        CmsPageEntity page = new CmsPageEntity(slug, req.getTitle().trim(), req.getStatus());
        pages.create(page);

        if (req.getBlocks() != null) {
            createBlocks(page.getId(), req.getBlocks());
        }
        return CmsPageResponseDto.from(page, toBlockDtos(page.getId()));
    }

    @Transactional
    @Override
    public CmsPageResponseDto updatePage(Long pageId, AdminCmsPageRequestDto req) {
        CmsPageEntity page = findPageOrThrow(pageId);

        String slug = resolveSlug(req.getSlug(), req.getTitle());
        if (!slug.equals(page.getSlug()) && pages.findBySlug(slug) != null) {
            throw BusinessException.conflict(ErrorCode.DUPLICATE_SLUG, "Đường dẫn (slug) đã tồn tại: " + slug, slug);
        }
        page.setSlug(slug);
        page.setTitle(req.getTitle().trim());
        if (req.getStatus() != null && !req.getStatus().isBlank()) {
            page.setStatus(req.getStatus().trim().toUpperCase());
        }
        page.touch();
        pages.update(page);

        // blocks == null -> giữ nguyên blocks hiện có (PUT chỉ sửa thông tin page). blocks != null
        // (kể cả rỗng []) -> THAY TOÀN BỘ: xoá hết block cũ rồi tạo lại theo danh sách mới, cùng cách
        // PromotionServiceImpl.update() thay productIds (mục O spec: "Cập nhật page + block").
        if (req.getBlocks() != null) {
            for (CmsBlockEntity old : blocks.findByPageId(pageId)) {
                blocks.delete(old);
            }
            createBlocks(pageId, req.getBlocks());
        }
        return CmsPageResponseDto.from(page, toBlockDtos(pageId));
    }

    @Transactional
    @Override
    public CmsBlockResponseDto addBlock(Long pageId, AdminCmsBlockRequestDto req) {
        findPageOrThrow(pageId);
        CmsBlockEntity block = new CmsBlockEntity(pageId, req.getType(), req.getSortOrder(), req.getData());
        blocks.create(block);
        return CmsBlockResponseDto.from(block);
    }

    @Transactional
    @Override
    public CmsBlockResponseDto updateBlock(Long pageId, Long blockId, AdminCmsBlockRequestDto req) {
        findPageOrThrow(pageId);
        CmsBlockEntity block = findBlockOrThrow(pageId, blockId);
        block.setType(req.getType());
        block.setSortOrder(req.getSortOrder());
        block.setData(req.getData());
        block.touch();
        blocks.update(block);
        return CmsBlockResponseDto.from(block);
    }

    @Transactional
    @Override
    public void deleteBlock(Long pageId, Long blockId) {
        findPageOrThrow(pageId);
        CmsBlockEntity block = findBlockOrThrow(pageId, blockId);
        blocks.delete(block);
    }

    private void createBlocks(Long pageId, List<AdminCmsBlockRequestDto> reqBlocks) {
        for (AdminCmsBlockRequestDto b : reqBlocks) {
            blocks.create(new CmsBlockEntity(pageId, b.getType(), b.getSortOrder(), b.getData()));
        }
    }

    private List<CmsBlockResponseDto> toBlockDtos(Long pageId) {
        return blocks.findByPageId(pageId).stream().map(CmsBlockResponseDto::from).toList();
    }

    private CmsPageEntity findPageOrThrow(Long pageId) {
        CmsPageEntity page = pages.findOne(pageId);
        if (page == null || page.getDeletedDate() != null) {
            throw new ResourceNotFoundException("Trang CMS", "CmsPage", pageId);
        }
        return page;
    }

    /** IDOR-kiểu: đảm bảo blockId thực sự thuộc pageId trên URL, không cho sửa/xoá nhầm block của
     * trang khác chỉ bằng cách đổi blockId. */
    private CmsBlockEntity findBlockOrThrow(Long pageId, Long blockId) {
        CmsBlockEntity block = blocks.findOne(blockId);
        if (block == null || !block.getPageId().equals(pageId)) {
            throw new ResourceNotFoundException("Block CMS", "CmsBlock", blockId);
        }
        return block;
    }

    private static String resolveSlug(String slug, String title) {
        return (slug == null || slug.isBlank()) ? SlugUtil.slugify(title) : SlugUtil.slugify(slug);
    }
}
