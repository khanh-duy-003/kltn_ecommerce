package com.pk.core.business.service.impl;

import lombok.RequiredArgsConstructor;

import com.pk.core.business.repository.PromotionRepo;
import com.pk.core.business.service.PromotionService;
import com.pk.core.common.exception.ResourceNotFoundException;
import com.pk.core.model.dto.request.AdminPromotionRequestDto;
import com.pk.core.model.dto.response.PromotionResponseDto;
import com.pk.core.model.entity.PromotionEntity;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepo promotions;

    @Transactional(readOnly = true)
    @Override
    public List<PromotionResponseDto> findAll() {
        // findAll() (PkRepo/ScannableRepository) không tự lọc deleted_date - lọc tay ở đây (chưa có
        // tiền lệ SQL tự lọc soft-delete trong dự án, xem ProductRepo cho cách làm ở tầng SQL thay
        // vì đợt này giữ đơn giản ở tầng Java vì danh sách promotion không lớn).
        return promotions.findAll(Sort.unsorted()).stream()
                .filter(p -> p.getDeletedDate() == null)
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public PromotionResponseDto findById(Long promotionId) {
        return toDto(requirePromotion(promotionId));
    }

    @Transactional
    @Override
    public PromotionResponseDto create(AdminPromotionRequestDto req) {
        PromotionEntity p = new PromotionEntity();
        applyRequest(p, req);
        promotions.create(p);
        linkProducts(p.getId(), req.getProductIds());
        return toDto(p);
    }

    @Transactional
    @Override
    public PromotionResponseDto update(Long promotionId, AdminPromotionRequestDto req) {
        PromotionEntity p = requirePromotion(promotionId);
        applyRequest(p, req);
        p.touch();
        promotions.update(p);
        // Thay toàn bộ danh sách sản phẩm (xoá hết liên kết cũ rồi ghi lại) thay vì diff thêm/bớt -
        // đơn giản hơn, đủ dùng cho phạm vi đồ án (xem PromotionRepo javadoc).
        promotions.deleteAllByPromotionId(promotionId);
        linkProducts(promotionId, req.getProductIds());
        return toDto(p);
    }

    @Transactional
    @Override
    public void delete(Long promotionId) {
        // Xoá mềm (set deletedDate), giống ProductServiceImpl.softDelete - bảng promotions có sẵn
        // cột deleted_id/deleted_date (extends BaseEntity) nên dùng đúng cột đó thay vì xoá thật.
        PromotionEntity p = requirePromotion(promotionId);
        p.setDeletedDate(new java.util.Date());
        p.touch();
        promotions.update(p);
    }

    private void linkProducts(Long promotionId, List<Long> productIds) {
        for (Long productId : productIds) {
            promotions.addProduct(promotionId, productId);
        }
    }

    private void applyRequest(PromotionEntity p, AdminPromotionRequestDto req) {
        p.setName(req.getName().trim());
        p.setDiscountType(req.getDiscountType().trim().toUpperCase(Locale.ROOT));
        p.setDiscountValue(req.getDiscountValue());
        p.setMaxDiscountAmount(req.getMaxDiscountAmount());
        p.setStartsAt(req.getStartsAt());
        p.setEndsAt(req.getEndsAt());
        if (req.getStatus() != null && !req.getStatus().isBlank()) {
            p.setStatus(req.getStatus().trim().toUpperCase(Locale.ROOT));
        }
    }

    private PromotionEntity requirePromotion(Long promotionId) {
        PromotionEntity p = promotions.findOne(promotionId);
        if (p == null || p.getDeletedDate() != null) {
            throw new ResourceNotFoundException("Chương trình khuyến mãi", "Promotion", promotionId);
        }
        return p;
    }

    private PromotionResponseDto toDto(PromotionEntity p) {
        return PromotionResponseDto.from(p, promotions.findProductIdsByPromotionId(p.getId()));
    }
}
