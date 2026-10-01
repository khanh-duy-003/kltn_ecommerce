package com.pk.core.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/** Body cho POST /storefront/payment/{paymentId}/callback (webhook cổng thanh toán gọi vào). */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCallbackRequestDto {

    @NotBlank
    private String provider;

    @NotBlank
    private String transactionId;

    @NotBlank
    private String orderCode;

    @NotBlank
    private String status;

    @NotNull
    private BigDecimal amount;

    @NotBlank
    private String signature;
}
