package com.pk.core.model.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** POST /admin/pre-orders (spec FE: {enabled (bắt buộc), message}). */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PreOrderConfigRequestDto {

    @NotNull
    private Boolean enabled;

    @Size(max = 500)
    private String message;
}
