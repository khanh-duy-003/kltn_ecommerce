package com.pk.core.model.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/** Body cho POST /admin/orders/{orderCode}/refund (mục L spec). Chỉ hoàn tiền được khi đơn đang
 * paymentStatus=PAID (xem OrderServiceImpl.refund()). `amount` hiện CHƯA lưu vào cột riêng (bảng
 * `orders`/`payments` không có cột refund_amount) - chỉ ghi vào OrderTimelineEntity.note, đủ dùng
 * cho quy mô đồ án (cố ý đơn giản hoá, giống cách productDiscount/shippingFee để 0 ở OrderService). */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminOrderRefundRequestDto {

    @NotNull(message = "Số tiền hoàn không được để trống")
    @DecimalMin(value = "0.01", message = "Số tiền hoàn phải lớn hơn 0")
    private BigDecimal amount;

    @NotBlank(message = "Lý do hoàn tiền không được để trống")
    @Size(max = 500)
    private String reason;
}
