package com.pk.core.test.business;

import com.pk.core.business.repository.PromotionRepo;
import com.pk.core.business.service.impl.PromotionPricingServiceImpl;
import com.pk.core.model.entity.PromotionEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PromotionPricingTest {

    @Mock PromotionRepo promotions;

    private static PromotionEntity promo(String type, long value, Long max) {
        PromotionEntity p = new PromotionEntity();
        p.setName("KM " + type);
        p.setDiscountType(type);
        p.setDiscountValue(BigDecimal.valueOf(value));
        p.setMaxDiscountAmount(max == null ? null : BigDecimal.valueOf(max));
        p.setStartsAt(new Date(System.currentTimeMillis() - 3_600_000));
        p.setEndsAt(new Date(System.currentTimeMillis() + 3_600_000));
        p.setStatus(PromotionEntity.PUBLISHED);
        return p;
    }

    private static BigDecimal bd(long v) {
        return BigDecimal.valueOf(v);
    }

    @Test
    void percentRespectsMaxDiscountCap() {
        PromotionEntity p = promo(PromotionEntity.PERCENT, 20, 150_000L);
        assertEquals(0, bd(150_000).compareTo(p.computeUnitDiscount(bd(1_000_000))));
        assertEquals(0, bd(40_000).compareTo(p.computeUnitDiscount(bd(200_000))));
    }

    @Test
    void fixedNeverExceedsUnitPrice() {
        PromotionEntity p = promo(PromotionEntity.FIXED, 300_000, null);
        assertEquals(0, bd(300_000).compareTo(p.computeUnitDiscount(bd(1_000_000))));
        assertEquals(0, bd(200_000).compareTo(p.computeUnitDiscount(bd(200_000))));
    }

    @Test
    void flatPriceOnlyAppliesWhenCheaperThanCurrentPrice() {
        PromotionEntity p = promo(PromotionEntity.FLAT_PRICE, 700_000, null);
        assertEquals(0, bd(300_000).compareTo(p.computeUnitDiscount(bd(1_000_000))));
        assertEquals(0, BigDecimal.ZERO.compareTo(p.computeUnitDiscount(bd(600_000))));
    }

    @Test
    void activeWindowAndStatusAreChecked() {
        PromotionEntity p = promo(PromotionEntity.PERCENT, 10, null);
        assertTrue(p.isActiveAt(new Date()));
        p.setStatus(PromotionEntity.DRAFT);
        assertFalse(p.isActiveAt(new Date()));
        p.setStatus(PromotionEntity.PUBLISHED);
        assertFalse(p.isActiveAt(new Date(System.currentTimeMillis() + 7_200_000)));
    }

    @Test
    void largestDiscountWinsAndDoesNotStack() {
        PromotionPricingServiceImplHolder h = new PromotionPricingServiceImplHolder(promotions);
        when(promotions.findActiveByProductId(eq(5L), any(Date.class))).thenReturn(List.of(
                promo(PromotionEntity.PERCENT, 10, null),      // 100k
                promo(PromotionEntity.FIXED, 250_000, null),   // 250k  <- lớn nhất
                promo(PromotionEntity.FLAT_PRICE, 950_000, null))); // 50k

        assertEquals(0, bd(250_000).compareTo(h.service.unitDiscount(5L, bd(1_000_000))));
    }

    @Test
    void noPromotionMeansZero() {
        PromotionPricingServiceImplHolder h = new PromotionPricingServiceImplHolder(promotions);
        when(promotions.findActiveByProductId(eq(6L), any(Date.class))).thenReturn(List.of());

        assertEquals(0, BigDecimal.ZERO.compareTo(h.service.unitDiscount(6L, bd(1_000_000))));
    }

    /** Gói khởi tạo để test ngắn gọn. */
    private static final class PromotionPricingServiceImplHolder {
        final PromotionPricingServiceImpl service;

        PromotionPricingServiceImplHolder(PromotionRepo repo) {
            this.service = new PromotionPricingServiceImpl(repo);
        }
    }
}
