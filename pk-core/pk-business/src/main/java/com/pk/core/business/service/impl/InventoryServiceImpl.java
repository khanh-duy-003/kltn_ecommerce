package com.pk.core.business.service.impl;

import lombok.RequiredArgsConstructor;

import com.pk.core.business.repository.ProductSkuRepo;
import com.pk.core.business.service.InventoryService;
import com.pk.core.common.exception.BusinessException;
import com.pk.core.common.exception.ErrorCode;
import com.pk.core.common.exception.ResourceNotFoundException;
import com.pk.core.model.dto.request.AdminStockAdjustmentRequestDto;
import com.pk.core.model.dto.response.StockLevelResponseDto;
import com.pk.core.model.entity.ProductSkuEntity;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    /** Kho ngầm định duy nhất - xem javadoc InventoryService. */
    public static final String MAIN_WAREHOUSE_ID = "MAIN";

    private static final Set<String> REASONS = Set.of("RECEIPT", "DAMAGE", "CORRECTION");

    private final ProductSkuRepo productSkus;

    @Transactional(readOnly = true)
    @Override
    public List<StockLevelResponseDto> stockLevels(String keyword) {
        String kw = keyword != null ? keyword.trim().toLowerCase() : "";
        return productSkus.findAll(Sort.unsorted()).stream()
                .filter(s -> kw.isBlank()
                        || s.getSkuCode().toLowerCase().contains(kw)
                        || (s.getName() != null && s.getName().toLowerCase().contains(kw)))
                .map(s -> StockLevelResponseDto.from(s, MAIN_WAREHOUSE_ID))
                .toList();
    }

    @Transactional
    @Override
    public StockLevelResponseDto adjust(AdminStockAdjustmentRequestDto req) {
        if (!REASONS.contains(req.getReason())) {
            throw BusinessException.badRequest(ErrorCode.VALIDATION_FAILED,
                    "reason không hợp lệ (chỉ nhận RECEIPT/DAMAGE/CORRECTION): " + req.getReason(), req.getReason());
        }
        ProductSkuEntity sku = productSkus.findOne(req.getSkuId());
        if (sku == null) {
            throw new ResourceNotFoundException("Biến thể sản phẩm", "ProductSku", req.getSkuId());
        }

        int delta = req.getQuantityDelta();
        int prospective = sku.getOnHand() + delta;
        // args khớp placeholder {0} của messages_vi/en (NEGATIVE_STOCK) - kiểm tra trước ở Java để có
        // số liệu cho thông báo, rồi vẫn gọi adjustOnHand() (UPDATE có điều kiện nguyên tử) làm chốt
        // chặn cuối cùng chống race condition giữa lúc đọc và lúc ghi.
        if (prospective < 0) {
            throw BusinessException.unprocessable(ErrorCode.NEGATIVE_STOCK,
                    "Điều chỉnh tồn kho không hợp lệ: số lượng sau điều chỉnh sẽ âm (" + prospective + ")",
                    prospective);
        }
        if (productSkus.adjustOnHand(req.getSkuId(), delta) == 0) {
            throw BusinessException.unprocessable(ErrorCode.NEGATIVE_STOCK,
                    "Tồn kho vừa thay đổi bởi yêu cầu khác, điều chỉnh này sẽ khiến số lượng âm - thử lại",
                    prospective);
        }

        ProductSkuEntity updated = productSkus.findOne(req.getSkuId());
        return StockLevelResponseDto.from(updated, MAIN_WAREHOUSE_ID);
    }
}
