package com.pk.core.business.repository;

import com.pk.core.model.entity.PhoneOtpEntity;

import org.springframework.data.repository.query.Param;

public interface PhoneOtpRepo extends PkRepo<PhoneOtpEntity, Long> {

    /** OTP mới nhất của SĐT theo mục đích (REGISTER / RESET_PASSWORD). Trả null nếu chưa từng gửi. */
    PhoneOtpEntity findLatest(@Param("phone") String phone, @Param("purpose") String purpose);

    /** Tra theo SHA-256 của registration token. Trả null nếu không có. */
    PhoneOtpEntity findByRegistrationTokenHash(@Param("tokenHash") String tokenHash);
}
