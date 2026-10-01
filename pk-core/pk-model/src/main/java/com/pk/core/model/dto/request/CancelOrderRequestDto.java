package com.pk.core.model.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Body cho POST /storefront/order/{orderCode}/cancel. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CancelOrderRequestDto {

    @Size(max = 500)
    private String reason;
}
