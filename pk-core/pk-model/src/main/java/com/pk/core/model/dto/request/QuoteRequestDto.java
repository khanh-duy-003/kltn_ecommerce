package com.pk.core.model.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/** Body cho POST /storefront/checkout/quote - tính lại giá trước khi đặt hàng, KHÔNG giữ chỗ tồn
 * kho/KHÔNG tăng lượt dùng voucher (chỉ xem trước, xem CheckoutService). */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuoteRequestDto {

    @NotNull
    private Long addressId;

    @NotBlank
    @Size(max = 30)
    private String shippingMethod;

    @NotBlank
    @Size(max = 30)
    private String paymentMethod;

    @Size(max = 40)
    private String voucherCode;

    @NotEmpty
    @Valid
    private List<OrderItemRequestDto> items;
}
