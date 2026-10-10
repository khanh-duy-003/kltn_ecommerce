package com.pk.core.business.service;

import com.pk.core.model.dto.request.QuoteRequestDto;
import com.pk.core.model.dto.response.QuoteResponseDto;

public interface CheckoutService {

    /** Xem trước giá đơn hàng trước khi đặt (POST /storefront/checkout/quote) - dùng lại đúng cách
     * tính giá/voucher như OrderServiceImpl.create, nhưng KHÔNG giữ chỗ tồn kho (reserveStock) và
     * KHÔNG tăng lượt dùng voucher (chỉ xem trước, có thể gọi nhiều lần không tác dụng phụ). Vẫn
     * kiểm tra đủ hàng (available >= qty) để báo lỗi sớm, nhưng không "giữ" - đơn thật vẫn có thể
     * hết hàng giữa lúc quote và lúc đặt (giống mọi sàn TMĐT khác, chấp nhận được). */
    QuoteResponseDto quote(Long userId, QuoteRequestDto req);

    /** Báo giá cho khách vãng lai (không cần đăng nhập). */
    QuoteResponseDto quoteGuest(com.pk.core.model.dto.request.GuestOrderRequestDto req);
}
