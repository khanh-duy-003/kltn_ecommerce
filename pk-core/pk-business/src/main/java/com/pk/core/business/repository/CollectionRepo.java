package com.pk.core.business.repository;

import com.pk.core.model.entity.CollectionEntity;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CollectionRepo extends PkRepo<CollectionEntity, Long> {

    /** Trả null nếu không có. */
    CollectionEntity findBySlug(@Param("slug") String slug);

    List<CollectionEntity> findAllPublished();

    /** Các collection (đã publish hoặc chưa) mà sản phẩm thuộc về - dùng để dựng trường
     * `collections[]` của ProductResponseDto theo spec FE. */
    List<CollectionEntity> findByProductId(@Param("productId") Long productId);
}
