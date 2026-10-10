package com.pk.core.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    private String recipientName;
    /** FE gửi firstName (tên) + lastName (họ + đệm) thay cho recipientName. */
    private String firstName;
    private String lastName;

    @NotBlank
    @Pattern(regexp = "^[0-9+ ]{8,20}$", message = "Số điện thoại không hợp lệ")
    @JsonAlias("receiverPhone")
    private String phone;

    @NotBlank
    @Size(max = 100)
    @JsonAlias("provinceName")
    private String province;

    @Size(max = 100)
    private String district;

    @NotBlank
    @Size(max = 100)
    @JsonAlias("wardName")
    private String ward;

    @NotBlank
    @Size(max = 255)
    private String addressLine;

    @JsonProperty("isDefault")
    private boolean isDefault;

    /** recipientName, hoặc ghép "lastName firstName" của FE khi không có. */
    @NotBlank
    @Size(max = 120)
    public String getRecipientName() {
        if (recipientName != null && !recipientName.isBlank()) {
            return recipientName;
        }
        String joined = ((lastName == null ? "" : lastName.trim()) + " " + (firstName == null ? "" : firstName.trim())).trim();
        return joined.isEmpty() ? null : joined;
    }

    /** DB bắt buộc district; FE không có quận/huyện nên mặc định chuỗi rỗng. */
    public String getDistrict() {
        return district == null ? "" : district;
    }
}
