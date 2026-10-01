package com.pk.core.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuoteResponseDto {

    private List<OrderItemResponseDto> items;
    private QuoteSummaryResponseDto summary;
    private Date estimatedDeliveryDate;
}
