package com.pk.core.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Body cho POST/PUT /storefront/me/addresses(/{id}). */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddressRequestDto {

    @NotBlank
    @Size(max = 120)
    private String recipientName;

    @NotBlank
    @Pattern(regexp = "^[0-9+ ]{8,20}$", message = "Số điện thoại không hợp lệ")
    private String phone;

    @NotBlank
    @Size(max = 100)
    private String province;

    @NotBlank
    @Size(max = 100)
    private String district;

    @NotBlank
    @Size(max = 100)
    private String ward;

    @NotBlank
    @Size(max = 255)
    private String addressLine;

    private boolean isDefault;
}
