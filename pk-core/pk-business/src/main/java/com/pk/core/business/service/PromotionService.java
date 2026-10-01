package com.pk.core.business.service;

import com.pk.core.model.dto.request.AdminPromotionRequestDto;
import com.pk.core.model.dto.response.PromotionResponseDto;

import java.util.List;

/** Chương trình khuyến mãi theo sản phẩm - Admin mục K spec (không có API storefront tra cứu riêng,
 * giống VoucherService - áp dụng ở nơi khác nếu cần, CHƯA có yêu cầu tích hợp vào OrderService đợt
 * này vì spec không mô tả rõ cách promotion tự động áp vào giá hiển thị storefront). */
public interface PromotionService {

    List<PromotionResponseDto> findAll();

    PromotionResponseDto findById(Long promotionId);

    PromotionResponseDto create(AdminPromotionRequestDto req);

    PromotionResponseDto update(Long promotionId, AdminPromotionRequestDto req);

    void delete(Long promotionId);
}
