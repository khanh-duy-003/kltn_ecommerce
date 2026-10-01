package com.pk.core.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuoteSummaryResponseDto {

    private BigDecimal subtotal;
    private BigDecimal productDiscount;
    private BigDecimal voucherDiscount;
    private BigDecimal shippingFee;
    private BigDecimal grandTotal;
    private int itemCount;
}
