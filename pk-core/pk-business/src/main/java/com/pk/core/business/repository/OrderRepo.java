package com.pk.core.business.repository;

import com.pk.core.model.entity.OrderEntity;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepo extends PkRepo<OrderEntity, Long> {

    /** Trả null nếu không có hoặc không thuộc user này - tránh lộ đơn của người khác (IDOR). */
    OrderEntity findByCodeAndUserId(@Param("code") String code, @Param("userId") Long userId);

    /** KHÔNG lọc theo user - chỉ dùng ở PaymentServiceImpl (webhook cổng thanh toán gọi vào, không
     * có người dùng đăng nhập nên không có userId để lọc theo IDOR như findByCodeAndUserId). */
    OrderEntity findByCode(@Param("code") String code);

    List<OrderEntity> findByUserId(@Param("userId") Long userId);

    /** Tìm đơn cho Admin mục L (GET /admin/orders) - lọc rỗng = trả tất cả (cùng quy ước
     * ProductRepo.searchAdmin). fromDate/toDate KHÔNG lọc ở đây (đọc toàn bộ rồi lọc placedAt bằng
     * Java ở OrderServiceImpl - tránh đoán sai cú pháp bind kiểu Date của Mirage, xem
     * OrderServiceImpl.searchForAdmin()). */
    List<OrderEntity> searchAdmin(@Param("status") String status, @Param("paymentStatus") String paymentStatus,
                                   @Param("keyword") String keyword);
}
