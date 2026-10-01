package com.pk.core.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Body cho PATCH /admin/orders/{orderCode}/status (mục L spec). `status` phải là 1 trong các hằng
 * OrderEntity.PENDING/CONFIRMED/SHIPPING/DELIVERED/CANCELLED - khớp OrderEntity.NEXT_STATUSES
 * (không dùng RETURNED ở đây, đi qua POST .../return riêng). Sai luồng chuyển trạng thái ->
 * BusinessException.conflict(ErrorCode.INVALID_STATUS_TRANSITION) (409, xem OrderServiceImpl). */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminOrderStatusRequestDto {

    @NotBlank(message = "Trạng thái không được để trống")
    private String status;

    @Size(max = 500)
    private String note;

    /** Tuỳ chọn - thường điền khi chuyển sang SHIPPING. */
    @Size(max = 100)
    private String trackingCode;
}
