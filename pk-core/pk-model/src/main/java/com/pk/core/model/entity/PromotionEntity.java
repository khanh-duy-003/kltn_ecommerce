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

/** Chương trình khuyến mãi theo sản phẩm (Admin mục K spec, bảng `promotions` đã có từ V1__init.sql
 * nhưng chưa có entity - tạo lúc dựng Admin Promotion & Voucher, 2026-09-29). Khác VoucherEntity:
 * áp trực tiếp lên sản phẩm cụ thể (qua bảng nối promotion_products), không cần khách nhập mã. */
@Getter
@Setter
@NoArgsConstructor
@Table(name = TableConstant.PROMOTIONS)
public class PromotionEntity extends BaseEntity {

    public static final String PERCENT = "PERCENT";
    public static final String FIXED = "FIXED";
    public static final String FLAT_PRICE = "FLAT_PRICE";
    public static final String DRAFT = "DRAFT";
    public static final String PUBLISHED = "PUBLISHED";

    @Id
    @PrimaryKey(generationType = GenerationType.SEQUENCE, generator = "promotions_id_seq")
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "discount_type")
    private String discountType;

    @Column(name = "discount_value")
    private BigDecimal discountValue;

    @Column(name = "max_discount_amount")
    private BigDecimal maxDiscountAmount;

    @Column(name = "starts_at")
    private Date startsAt;

    @Column(name = "ends_at")
    private Date endsAt;

    @Column(name = "status")
    private String status = DRAFT;

    /** Khuyến mãi đang hiệu lực tại thời điểm `now`: PUBLISHED, chưa xoá mềm và nằm trong [startsAt, endsAt]. */
    public boolean isActiveAt(Date now) {
        return PUBLISHED.equals(status) && getDeletedDate() == null
                && startsAt != null && endsAt != null
                && !now.before(startsAt) && !now.after(endsAt);
    }

    /** Số tiền giảm TRÊN 1 ĐƠN VỊ sản phẩm có giá `unitPrice` (luôn trong [0, unitPrice], làm tròn về đồng):
     * PERCENT = unitPrice * value / 100 (chặn trên bởi maxDiscountAmount nếu có); FIXED = trừ thẳng
     * value đồng; FLAT_PRICE = đồng giá, bán còn đúng value đồng (chỉ có tác dụng khi value < unitPrice). */
    public BigDecimal computeUnitDiscount(BigDecimal unitPrice) {
        if (unitPrice == null || unitPrice.signum() <= 0 || discountValue == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal discount;
        if (PERCENT.equals(discountType)) {
            discount = unitPrice.multiply(discountValue).divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP);
            if (maxDiscountAmount != null && maxDiscountAmount.signum() > 0) {
                discount = discount.min(maxDiscountAmount);
            }
        } else if (FIXED.equals(discountType)) {
            discount = discountValue;
        } else if (FLAT_PRICE.equals(discountType)) {
            discount = unitPrice.subtract(discountValue);
        } else {
            return BigDecimal.ZERO;
        }
        return discount.max(BigDecimal.ZERO).min(unitPrice);
    }
}
