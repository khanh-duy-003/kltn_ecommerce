package com.pk.core.business.repository;

import com.pk.core.model.entity.CartEntity;

import org.springframework.data.repository.query.Param;

import vn.com.unit.springframework.data.mirage.repository.query.Modifying;

public interface CartRepo extends PkRepo<CartEntity, Long> {

    /** Giỏ của khách đăng nhập. Trả null nếu chưa có. */
    CartEntity findByUserId(@Param("userId") Long userId);

    /** Giỏ của khách vãng lai theo guestId (header X-Guest-Cart-Id). Trả null nếu chưa có. */
    CartEntity findByGuestId(@Param("guestId") String guestId);

    /** Xoá giỏ (gọi SAU khi đã xoá hết dòng bằng CartItemRepo.deleteByCartId). */
    @Modifying
    int deleteCart(@Param("id") Long id);
}
