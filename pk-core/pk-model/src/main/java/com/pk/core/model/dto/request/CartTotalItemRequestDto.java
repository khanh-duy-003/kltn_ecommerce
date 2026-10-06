package com.pk.core.model.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/** Một dòng của {@link CartTotalRequestDto}; selectedPackagingRelationIds nhận nhưng bỏ qua (chưa có bao bì). */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartTotalItemRequestDto {

    /** Id SKU. */
    @NotBlank
    private String variationId;

    @Min(1)
    private int quantity;

    private List<String> selectedPackagingRelationIds;
}
