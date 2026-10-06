package com.pk.core.business.service.impl;

import com.pk.core.business.repository.PromotionRepo;
import com.pk.core.business.service.PromotionPricingService;
import com.pk.core.model.entity.PromotionEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class PromotionPricingServiceImpl implements PromotionPricingService {

    private final PromotionRepo promotions;

    @Transactional(readOnly = true)
    @Override
    public BigDecimal unitDiscount(Long productId, BigDecimal unitPrice) {
        if (productId == null || unitPrice == null || unitPrice.signum() <= 0) {
            return BigDecimal.ZERO;
        }
        Date now = new Date();
        BigDecimal best = BigDecimal.ZERO;
        for (PromotionEntity promotion : promotions.findActiveByProductId(productId, now)) {
            if (!promotion.isActiveAt(now)) {
                continue;
            }
            best = best.max(promotion.computeUnitDiscount(unitPrice));
        }
        return best;
    }
}
