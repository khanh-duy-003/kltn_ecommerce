package com.pk.core.identity.service.impl;

import com.pk.core.identity.service.OtpSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Chưa tích hợp SMS: chỉ ghi mã OTP vào log để dev/test lấy mã. KHÔNG dùng cho production thật (mã OTP
 * nằm trong log). Thay bằng bean gửi SMS thật khi triển khai.
 */
@Component
public class LogOtpSender implements OtpSender {

    private static final Logger log = LoggerFactory.getLogger(LogOtpSender.class);

    @Override
    public void send(String phone, String code, String purpose) {
        log.info("[OTP chưa tích hợp SMS] {} cho {}: {}", purpose, phone, code);
    }
}
