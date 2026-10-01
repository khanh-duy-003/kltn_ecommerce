package com.pk.core.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/** Tạo/sửa chương trình khuyến mãi theo sản phẩm (Admin mục K: POST/PUT /admin/promotions).
 * `discountType`: PERCENT/FIXED/FLAT_PRICE (xem PromotionEntity). */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminPromotionRequestDto {

    @NotBlank(message = "Tên chương trình không được để trống")
    private String name;

    @NotBlank(message = "Loại giảm giá không được để trống")
    private String discountType;

    @NotNull(message = "Giá trị giảm không được để trống")
    @PositiveOrZero(message = "Giá trị giảm không được âm")
    private BigDecimal discountValue;

    private BigDecimal maxDiscountAmount;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    private Date startsAt;

    @NotNull(message = "Ngày kết thúc không được để trống")
    private Date endsAt;

    private String status;

    @NotEmpty(message = "Phải áp dụng cho ít nhất 1 sản phẩm")
    private List<Long> productIds;
}
