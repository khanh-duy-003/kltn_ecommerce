package com.pk.core.model.entity;

import com.pk.core.model.constant.TableConstant;
import com.pk.core.common.entity.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import vn.com.unit.miragesql.miragesql.annotation.Column;
import vn.com.unit.miragesql.miragesql.annotation.PrimaryKey;
import vn.com.unit.miragesql.miragesql.annotation.PrimaryKey.GenerationType;
import vn.com.unit.miragesql.miragesql.annotation.Table;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

/** Voucher giảm giá toàn đơn, nhập mã lúc checkout (chưa có API riêng cho storefront tra cứu voucher
 * theo spec FE - chỉ dùng nội bộ trong OrderService khi tạo đơn). Đơn giản hoá: KHÔNG làm việc tăng
 * used_count ở entity (tăng nguyên tử bằng SQL UPDATE có điều kiện ở VoucherRepo.applyUsage, tránh
 * race condition giữa 2 request dùng cùng voucher gần hết lượt). */
@Getter
@Setter
@NoArgsConstructor
@Table(name = TableConstant.VOUCHERS)
public class VoucherEntity extends BaseEntity {

    public static final String PERCENT = "PERCENT";
    public static final String FIXED = "FIXED";
    public static final String DRAFT = "DRAFT";
    public static final String PUBLISHED = "PUBLISHED";

    @Id
    @PrimaryKey(generationType = GenerationType.SEQUENCE, generator = "vouchers_id_seq")
    @Column(name = "id")
    private Long id;

    @Column(name = "code")
    private String code;

    @Column(name = "discount_type")
    private String discountType;

    @Column(name = "discount_value")
    private BigDecimal discountValue;

    @Column(name = "max_discount_amount")
    private BigDecimal maxDiscountAmount;

    @Column(name = "min_order_value")
    private BigDecimal minOrderValue = BigDecimal.ZERO;

    @Column(name = "usage_limit")
    private Integer usageLimit;

    @Column(name = "used_count")
    private int usedCount;

    @Column(name = "starts_at")
    private Date startsAt;

    @Column(name = "ends_at")
    private Date endsAt;

    @Column(name = "status")
    private String status = DRAFT;

    public boolean isActiveAt(Date now) {
        return PUBLISHED.equals(status) && !now.before(startsAt) && !now.after(endsAt);
    }

    public boolean isUsageAvailable() {
        return usageLimit == null || usedCount < usageLimit;
    }

    /** Số tiền giảm cho `subtotal` - không vượt quá maxDiscountAmount (nếu có) và không vượt quá
     * chính subtotal (tránh voucher FIXED lớn hơn đơn làm tổng tiền âm). */
    public BigDecimal computeDiscount(BigDecimal subtotal) {
        BigDecimal raw = PERCENT.equals(discountType)
                ? subtotal.multiply(discountValue).divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP)
                : discountValue;
        if (maxDiscountAmount != null && raw.compareTo(maxDiscountAmount) > 0) {
            raw = maxDiscountAmount;
        }
        if (raw.compareTo(subtotal) > 0) {
            raw = subtotal;
        }
        return raw.max(BigDecimal.ZERO);
    }
}
