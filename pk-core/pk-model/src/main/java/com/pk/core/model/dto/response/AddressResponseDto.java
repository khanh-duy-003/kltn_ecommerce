package com.pk.core.model.dto.response;

import com.pk.core.common.dto.BaseDto;
import com.pk.core.model.entity.CustomerAddressEntity;
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
}
