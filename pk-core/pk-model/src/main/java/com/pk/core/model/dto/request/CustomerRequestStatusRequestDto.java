package com.pk.core.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** PATCH /admin/customer-request/{id}/status. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRequestStatusRequestDto {

    @NotBlank
    @Pattern(regexp = "^(NEW|UNPROCESSED|CONTACTED|IN_PROGRESS|COMPLETED)$", message = "status không hợp lệ")
    private String status;
}
