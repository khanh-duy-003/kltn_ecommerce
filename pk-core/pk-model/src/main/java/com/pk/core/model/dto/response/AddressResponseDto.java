package com.pk.core.model.dto.response;

import com.pk.core.common.dto.BaseDto;
import com.pk.core.model.entity.CustomerAddressEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponseDto extends BaseDto {

    private Long id;
    private String recipientName;
    private String phone;
    private String province;
    private String district;
    private String ward;
    private String addressLine;
    private boolean isDefault;

    public static AddressResponseDto from(CustomerAddressEntity a) {
        return BaseDto.of(new AddressResponseDto(a.getId(), a.getRecipientName(), a.getPhone(), a.getProvince(),
                a.getDistrict(), a.getWard(), a.getAddressLine(), a.isDefault()), a);
    }

    // ---- FE Address: field tính toán, giữ nguyên field cũ ----

    public String getFirstName() {
        return firstNameOf(recipientName);
    }

    public String getLastName() {
        return lastNameOf(recipientName);
    }

    public String getReceiverPhone() {
        return phone;
    }

    public String getWardName() {
        return ward;
    }

    public String getProvinceName() {
        return province;
    }

    @JsonProperty("isDefault")
    public boolean getIsDefaultFlag() {
        return isDefault;
    }

    /** Họ + đệm của tên đầy đủ (FE hiển thị "lastName firstName"); tên đơn thì để rỗng. */
    private static String lastNameOf(String full) {
        if (full == null) {
            return "";
        }
        String t = full.trim();
        int i = t.lastIndexOf(' ');
        return i < 0 ? "" : t.substring(0, i).trim();
    }

    /** Tên (từ cuối) của tên đầy đủ. */
    private static String firstNameOf(String full) {
        if (full == null) {
            return "";
        }
        String t = full.trim();
        return t.substring(t.lastIndexOf(' ') + 1);
    }
}
