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

/** Body cho POST /storefront/order. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequestDto {

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

    @Size(max = 500)
    private String note;

    @NotEmpty
    @Valid
    private List<OrderItemRequestDto> items;
}
