package com.pk.core.identity.service;

/**
 * Gửi mã OTP tới số điện thoại. Hiện chỉ có bản ghi log ({@code LogOtpSender}); khi tích hợp nhà cung
 * cấp SMS thật thì viết bean khác implement interface này (đánh dấu {@code @Primary} hoặc bỏ bean log).
 */
public interface OtpSender {

    /** @param purpose PhoneOtpEntity.REGISTER hoặc PhoneOtpEntity.RESET_PASSWORD */
    void send(String phone, String code, String purpose);
}
