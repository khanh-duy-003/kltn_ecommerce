package com.pk.core.business.service;

import com.pk.core.model.dto.request.PaymentCallbackRequestDto;

public interface PaymentService {

    /** Xử lý callback từ cổng thanh toán (VNPay/MoMo...) - xác thực chữ ký, đối chiếu số tiền, cập
     * nhật trạng thái Payment + Order.paymentStatus. Xem PaymentServiceImpl javadoc về giới hạn xác
     * thực chữ ký (CHƯA phải đúng thuật toán VNPay/MoMo thật). */
    void handleCallback(Long paymentId, PaymentCallbackRequestDto req);
}
