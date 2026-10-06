package com.pk.core.business.service;

import com.pk.core.model.dto.response.WishlistIdsResponseDto;
import com.pk.core.model.dto.response.WishlistItemResponseDto;
import com.pk.core.model.dto.response.WishlistStatusResponseDto;

import java.util.List;

/** Sản phẩm yêu thích của khách đăng nhập (/storefront/product/customer/wishlist). Thêm 2026-10-06. */
public interface WishlistService {

    /** Danh sách yêu thích, mới thêm nhất lên đầu; bỏ qua sản phẩm đã xoá/không còn PUBLISHED. */
    List<WishlistItemResponseDto> list(Long userId);

    /** Chỉ các id sản phẩm yêu thích (cho FE tô trái tim nhanh). */
    WishlistIdsResponseDto ids(Long userId);

    /** Thêm các sản phẩm vào yêu thích (đã có thì bỏ qua - idempotent). Sản phẩm không tồn tại -> 404. Trả danh sách id mới. */
    WishlistIdsResponseDto add(Long userId, List<String> productIds);

    /** Bỏ yêu thích (không có thì bỏ qua - idempotent). Trả danh sách id còn lại. */
    WishlistIdsResponseDto remove(Long userId, List<String> productIds);

    /** Sản phẩm này đã được khách yêu thích chưa. */
    WishlistStatusResponseDto status(Long userId, String productId);
}
