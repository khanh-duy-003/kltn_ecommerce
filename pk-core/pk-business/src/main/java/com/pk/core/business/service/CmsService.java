package com.pk.core.business.service;

import com.pk.core.model.dto.request.AdminCmsBlockRequestDto;
import com.pk.core.model.dto.request.AdminCmsPageRequestDto;
import com.pk.core.model.dto.response.CmsBlockResponseDto;
import com.pk.core.model.dto.response.CmsPageResponseDto;

import java.util.List;

/** Admin mục O spec (document/09-tong-hop-api-fe.md). */
public interface CmsService {

    List<CmsPageResponseDto> findAllPages();

    CmsPageResponseDto findPageById(Long pageId);

    /** POST /admin/cms/pages - tạo page, seed blocks ban đầu nếu request.blocks != null. */
    CmsPageResponseDto createPage(AdminCmsPageRequestDto req);

    /** PUT /admin/cms/pages/{pageId} - cập nhật thông tin page; nếu request.blocks != null thì THAY
     * TOÀN BỘ blocks hiện có (xoá hết rồi tạo lại theo danh sách mới). */
    CmsPageResponseDto updatePage(Long pageId, AdminCmsPageRequestDto req);

    /** POST /admin/cms/pages/{pageId}/blocks - thêm 1 block mới vào cuối trang. */
    CmsBlockResponseDto addBlock(Long pageId, AdminCmsBlockRequestDto req);

    /** PUT /admin/cms/pages/{pageId}/blocks/{blockId} - sửa 1 block đã có. */
    CmsBlockResponseDto updateBlock(Long pageId, Long blockId, AdminCmsBlockRequestDto req);

    /** DELETE /admin/cms/pages/{pageId}/blocks/{blockId} - xoá (HARD delete) 1 block. */
    void deleteBlock(Long pageId, Long blockId);
}
