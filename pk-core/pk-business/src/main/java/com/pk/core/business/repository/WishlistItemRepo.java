package com.pk.core.business.repository;

import com.pk.core.model.entity.WishlistItemEntity;

import org.springframework.data.repository.query.Param;

import vn.com.unit.springframework.data.mirage.repository.query.Modifying;

import java.util.List;

public interface WishlistItemRepo extends PkRepo<WishlistItemEntity, Long> {

    /** Yêu thích của một khách, mới thêm nhất lên đầu. */
    List<WishlistItemEntity> findByUserId(@Param("userId") Long userId);

    /** Trả null nếu sản phẩm này chưa được khách yêu thích. */
    WishlistItemEntity findByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);

    @Modifying
    int deleteByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);
}
