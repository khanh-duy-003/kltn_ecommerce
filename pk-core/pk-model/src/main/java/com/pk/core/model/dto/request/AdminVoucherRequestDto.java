package com.pk.core.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/** Tạo/sửa voucher (Admin mục K: POST/PUT /admin/vouchers). `discountType`: PERCENT/FIXED (xem
 * VoucherEntity). */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminVoucherRequestDto {

    @NotBlank(message = "Mã voucher không được để trống")
    private String code;

    @NotBlank(message = "Loại giảm giá không được để trống")
    private String discountType;

    @NotNull(message = "Giá trị giảm không được để trống")
    @PositiveOrZero(message = "Giá trị giảm không được âm")
    private BigDecimal discountValue;

    private BigDecimal maxDiscountAmount;

    @PositiveOrZero(message = "Giá trị đơn tối thiểu không được âm")
    private BigDecimal minOrderValue;

    private Integer usageLimit;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    private Date startsAt;

    @NotNull(message = "Ngày kết thúc không được để trống")
    private Date endsAt;

    private String status;
}
