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
}
