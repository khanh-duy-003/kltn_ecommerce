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
}
