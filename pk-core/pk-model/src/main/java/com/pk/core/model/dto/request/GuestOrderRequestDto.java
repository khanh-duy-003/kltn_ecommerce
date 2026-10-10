package com.pk.core.model.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/** Đặt hàng / báo giá cho khách vãng lai: địa chỉ giao hàng gửi trực tiếp trong request (không lưu sổ địa chỉ). */
@Getter
@Setter
@NoArgsConstructor
public class GuestOrderRequestDto {

    @NotBlank
    @Size(max = 120)
    private String recipientName;

    @NotBlank
    @Pattern(regexp = "^[0-9+ .-]{8,20}$", message = "Số điện thoại không hợp lệ")
    private String phone;

    @Email
    @Size(max = 150)
    private String email;

    @NotBlank
    @Size(max = 100)
    private String province;

    @Size(max = 100)
    private String district;

    @Size(max = 100)
    private String ward;

    @NotBlank
    @Size(max = 255)
    private String addressLine;

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

    public String getDistrict() {
        return district == null ? "" : district;
    }

    public String getWard() {
        return ward == null ? "" : ward;
    }
}
