package com.pk.core.model.dto.response;

import com.pk.core.model.entity.RoleEntity;
import com.pk.core.model.entity.UserEntity;

import java.util.List;
import com.pk.core.common.dto.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto extends BaseDto {

    private Long id;
    private String email;
    private String fullName;
    private String phone;
    private List<String> roles;

    public static UserResponseDto from(UserEntity u) {
        return BaseDto.of(new UserResponseDto(u.getId(), u.getEmail(), u.getFullName(), u.getPhone(),
                u.getRoles().stream().map(RoleEntity::getName).sorted().toList()), u);
    }

    // ---- FE AuthUser: field tính toán, giữ nguyên field cũ ----

    public String getFirstName() {
        return firstNameOf(fullName);
    }

    public String getLastName() {
        return lastNameOf(fullName);
    }

    public String getStatus() {
        return "ACTIVE";
    }

    public String getType() {
        return "CUSTOMER";
    }

    public String getCreatedAt() {
        return getCreatedDate() == null ? null : getCreatedDate().toInstant().toString();
    }

    public String getUpdatedAt() {
        return getUpdatedDate() == null ? null : getUpdatedDate().toInstant().toString();
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
