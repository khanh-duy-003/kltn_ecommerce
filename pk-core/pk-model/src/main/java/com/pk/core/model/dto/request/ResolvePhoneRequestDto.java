package com.pk.core.model.dto.request;

import com.pk.core.common.util.PhoneUtil;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** POST /storefront/auth/resolve-phone. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResolvePhoneRequestDto {

    /** Dạng nhập: 0xxxxxxxxx, 84xxxxxxxxx hoặc +84xxxxxxxxx (service chuẩn hoá về 0xxxxxxxxx). */
    @NotBlank
    @Pattern(regexp = PhoneUtil.INPUT_REGEX, message = "Số điện thoại không hợp lệ")
    private String phone;
}
