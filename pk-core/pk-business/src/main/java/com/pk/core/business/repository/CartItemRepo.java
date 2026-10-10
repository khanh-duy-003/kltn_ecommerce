package com.pk.core.business.repository;

import com.pk.core.model.entity.CartItemEntity;

import org.springframework.data.repository.query.Param;

import vn.com.unit.springframework.data.mirage.repository.query.Modifying;

import java.util.List;

public interface CartItemRepo extends PkRepo<CartItemEntity, Long> {

    /** Các dòng của giỏ, theo thứ tự thêm vào (id tăng dần). */
    List<CartItemEntity> findByCartId(@Param("cartId") Long cartId);

    /** Trả null nếu SKU này chưa có trong giỏ. */
    CartItemEntity findByCartIdAndSkuId(@Param("cartId") Long cartId, @Param("skuId") Long skuId);

    @Modifying
    int deleteByCartId(@Param("cartId") Long cartId);

    /** Xoá đúng 1 dòng (SKU) khỏi giỏ. Trả 1 nếu có xoá. */
    @Modifying
    int deleteByCartIdAndSkuId(@Param("cartId") Long cartId, @Param("skuId") Long skuId);
}
