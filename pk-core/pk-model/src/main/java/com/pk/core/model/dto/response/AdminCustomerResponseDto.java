package com.pk.core.model.dto.response;

import com.pk.core.common.dto.BaseDto;
import com.pk.core.model.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Dòng danh sách GET /admin/customers (mục N spec). `orderCount` đếm bằng Java
 * (orders.findByUserId(id).size()) ở CustomerServiceImpl - đủ dùng cho quy mô đồ án, không cần
 * COUNT(*) riêng ở SQL. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminCustomerResponseDto extends BaseDto {

    private Long id;
    private String email;
    private String fullName;
    private String phone;
    private boolean enabled;
    private int orderCount;

    public static AdminCustomerResponseDto from(UserEntity u, int orderCount) {
        return BaseDto.of(new AdminCustomerResponseDto(u.getId(), u.getEmail(), u.getFullName(), u.getPhone(),
                u.isEnabled(), orderCount), u);
    }
}
