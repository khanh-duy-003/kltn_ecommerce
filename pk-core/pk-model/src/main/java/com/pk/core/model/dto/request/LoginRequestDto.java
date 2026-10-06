package com.pk.core.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDto {

    /** Số điện thoại đăng nhập (chấp nhận 0xxxxxxxxx, 84xxxxxxxxx, +84xxxxxxxxx - service tự chuẩn hoá). */
    @NotBlank
    private String phone;

    @NotBlank
    private String password;
}
