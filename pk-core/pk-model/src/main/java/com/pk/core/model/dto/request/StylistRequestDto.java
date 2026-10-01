package com.pk.core.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/** Body chung cho POST /storefront/ai/stylist/recommendations và POST /storefront/ai/set-builder
 * (spec FE mô tả cả 2 dùng chung dạng "nhu cầu": occasion, style[], budget, recipient). */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StylistRequestDto {

    @NotBlank
    private String occasion;

    private List<String> style;

    private BigDecimal budget;

    private String recipient;
}
