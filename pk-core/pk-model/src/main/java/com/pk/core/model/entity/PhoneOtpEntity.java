package com.pk.core.model.entity;

import com.pk.core.common.entity.UpdateEntity;
import com.pk.core.model.constant.TableConstant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import vn.com.unit.miragesql.miragesql.annotation.Column;
import vn.com.unit.miragesql.miragesql.annotation.PrimaryKey;
import vn.com.unit.miragesql.miragesql.annotation.PrimaryKey.GenerationType;
import vn.com.unit.miragesql.miragesql.annotation.Table;

import java.util.Date;

/**
 * Mã OTP gửi tới SĐT (đăng ký / quên mật khẩu). DB chỉ lưu SHA-256 của mã (lộ DB không lộ OTP).
 * Luồng đăng ký: send-otp tạo dòng mới -> verify-otp đúng thì đặt verifiedAt + sinh registration token
 * (cũng chỉ lưu hash) -> register/complete dùng token rồi consume. Luồng quên mật khẩu: reset-password
 * kiểm tra mã và consume ngay trong một bước.
 */
@Getter
@Setter
@NoArgsConstructor
@Table(name = TableConstant.PHONE_OTPS)
public class PhoneOtpEntity extends UpdateEntity {

    public static final String REGISTER = "REGISTER";
    public static final String RESET_PASSWORD = "RESET_PASSWORD";

    @Id
    @PrimaryKey(generationType = GenerationType.SEQUENCE, generator = "phone_otps_id_seq")
    @Column(name = "id")
    private Long id;

    /** Dạng chuẩn hoá của PhoneUtil (0xxxxxxxxx). */
    @Column(name = "phone")
    private String phone;

    /** REGISTER hoặc RESET_PASSWORD. */
    @Column(name = "purpose")
    private String purpose;

    @Column(name = "code_hash")
    private String codeHash;

    @Column(name = "expires_at")
    private Date expiresAt;

    /** Số lần nhập sai. */
    @Column(name = "attempts")
    private int attempts;

    @Column(name = "verified_at")
    private Date verifiedAt;

    @Column(name = "registration_token_hash")
    private String registrationTokenHash;

    @Column(name = "registration_expires_at")
    private Date registrationExpiresAt;

    @Column(name = "consumed_at")
    private Date consumedAt;

    public PhoneOtpEntity(String phone, String purpose, String codeHash, Date expiresAt) {
        this.phone = phone;
        this.purpose = purpose;
        this.codeHash = codeHash;
        this.expiresAt = expiresAt;
    }

    public boolean isExpired() { return expiresAt.before(new Date()); }

    public boolean isVerified() { return verifiedAt != null; }

    public boolean isConsumed() { return consumedAt != null; }

    public boolean isRegistrationExpired() {
        return registrationExpiresAt == null || registrationExpiresAt.before(new Date());
    }

    /** OTP đã nhập đúng: lưu hash của registration token và hạn dùng token. */
    public void markVerified(String tokenHash, Date tokenExpiresAt) {
        this.verifiedAt = new Date();
        this.registrationTokenHash = tokenHash;
        this.registrationExpiresAt = tokenExpiresAt;
    }

    public void consume() { if (consumedAt == null) consumedAt = new Date(); }
}
