package com.pk.core.business.service;

import com.pk.core.model.dto.request.CartSyncRequestDto;
import com.pk.core.model.dto.request.CartTotalRequestDto;
import com.pk.core.model.dto.response.CartRecommendationResponseDto;
import com.pk.core.model.dto.response.CartSyncResponseDto;
import com.pk.core.model.dto.response.CartTotalResponseDto;

/**
 * Giỏ hàng theo spec FE (/storefront/cart/cart). Giỏ thuộc về khách đăng nhập (userId) HOẶC khách vãng lai
 * (guestId do server cấp, FE gửi lại ở header X-Guest-Cart-Id). Khi cả hai cùng có, ưu tiên userId.
 * Giá là số nguyên VND. Thêm 2026-10-06.
 */
public interface CartService {

    /** Xem giỏ hiện tại. Chưa có giỏ -> danh sách rỗng (không tạo giỏ mới). */
    CartSyncResponseDto get(Long userId, String guestId);

    /**
     * Thêm vào giỏ (clearAll=false: cộng dồn số lượng) hoặc thay toàn bộ giỏ (clearAll=true). Số lượng bị
     * giới hạn theo tồn kho (available): vượt thì hạ về tối đa và trả success=false + reason/message; SKU
     * không tồn tại/không PUBLISHED hoặc hết hàng thì bỏ qua dòng đó. Phần còn lại của giỏ vẫn được lưu.
     * Khách vãng lai chưa có guestId -> server tạo mới và trả về trong response.
     */
    CartSyncResponseDto addOrSync(Long userId, String guestId, CartSyncRequestDto req);

    /** Gộp giỏ vãng lai (guestId) vào giỏ tài khoản rồi xoá giỏ vãng lai. Bắt buộc đã đăng nhập. */
    CartSyncResponseDto merge(Long userId, String guestId);

    /** Tính tổng tiền cho danh sách dòng gửi lên (không đụng giỏ trong DB). */
    CartTotalResponseDto calculateTotal(CartTotalRequestDto req);

    /** Gợi ý sản phẩm cho giỏ (cùng danh mục với hàng trong giỏ lên trước, mới nhất trước), page 1-based. */
    CartRecommendationResponseDto recommend(Long userId, String guestId, int page, int take);
}
