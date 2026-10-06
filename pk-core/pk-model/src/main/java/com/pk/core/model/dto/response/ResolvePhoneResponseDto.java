package com.pk.core.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Kết quả POST /storefront/auth/resolve-phone: FE dựa vào đây để chọn luồng đăng nhập hay đăng ký. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResolvePhoneResponseDto {

    /** SĐT đã có tài khoản chưa. */
    private boolean exists;

    /** Tài khoản có mật khẩu chưa (luôn true với tài khoản tạo qua luồng hiện tại). */
    private boolean hasPassword;

    /** Email đã che (vd. a***@gmail.com); null nếu chưa có tài khoản hoặc tài khoản không có email. */
    private String maskedEmail;
}
