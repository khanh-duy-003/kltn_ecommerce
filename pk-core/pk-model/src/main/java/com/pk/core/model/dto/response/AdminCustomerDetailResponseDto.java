package com.pk.core.model.dto.response;

import com.pk.core.common.dto.BaseDto;
import com.pk.core.model.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/** GET /admin/customers/{customerId} (mục N spec: "Hồ sơ + lịch sử đơn"). `orders` tái dùng thẳng
 * OrderResponseDto (không dựng bản rút gọn riêng) - `addresses` là phần mở rộng hợp lý (tái dùng
 * AddressResponseDto có sẵn), spec không cấm, giúp admin xem đủ hồ sơ khách. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminCustomerDetailResponseDto extends BaseDto {

    private Long id;
    private String email;
    private String fullName;
    private String phone;
    private boolean enabled;
    private List<AddressResponseDto> addresses;
    private List<OrderResponseDto> orders;

    public static AdminCustomerDetailResponseDto from(UserEntity u, List<AddressResponseDto> addresses,
                                                        List<OrderResponseDto> orders) {
        return BaseDto.of(new AdminCustomerDetailResponseDto(u.getId(), u.getEmail(), u.getFullName(),
                u.getPhone(), u.isEnabled(), addresses, orders), u);
    }
}
