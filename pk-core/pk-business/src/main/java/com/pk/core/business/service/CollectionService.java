package com.pk.core.business.service;

import com.pk.core.model.dto.request.AdminCollectionRequestDto;
import com.pk.core.model.dto.response.CollectionResponseDto;

import java.util.List;

public interface CollectionService {

    /** Bộ sưu tập đã publish (GET /storefront/product/collections). */
    List<CollectionResponseDto> findAllPublished();

    /** Toàn bộ bộ sưu tập (kể cả DRAFT) - Admin Catalog mục J: GET /admin/catalog/collections. */
    List<CollectionResponseDto> findAllForAdmin();

    /** Tạo bộ sưu tập - Admin Catalog mục J: POST /admin/catalog/collections. */
    CollectionResponseDto create(AdminCollectionRequestDto req);

    /** Sửa bộ sưu tập - Admin Catalog mục J: PUT /admin/catalog/collections/{collectionId}. */
    CollectionResponseDto update(Long collectionId, AdminCollectionRequestDto req);
}
