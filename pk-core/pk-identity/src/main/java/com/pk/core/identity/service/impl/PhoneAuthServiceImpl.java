package com.pk.core.identity.service.impl;

import lombok.RequiredArgsConstructor;

import com.pk.core.business.repository.PhoneOtpRepo;
import com.pk.core.business.repository.RoleRepo;
import com.pk.core.business.repository.UserRepo;
import com.pk.core.common.exception.BusinessException;
import com.pk.core.common.exception.ErrorCode;
import com.pk.core.common.util.PhoneUtil;
import com.pk.core.identity.security.jwt.JwtProvider;
import com.pk.core.identity.service.OtpSender;
import com.pk.core.identity.service.PhoneAuthService;
import com.pk.core.identity.service.RefreshTokenService;
import com.pk.core.model.dto.request.RegisterCompleteRequestDto;
import com.pk.core.model.dto.request.ResetPasswordRequestDto;
import com.pk.core.model.dto.request.VerifyOtpRequestDto;
import com.pk.core.model.dto.response.MessageResponseDto;
import com.pk.core.model.dto.response.OtpVerificationResponseDto;
import com.pk.core.model.dto.response.ResolvePhoneResponseDto;
import com.pk.core.model.dto.response.TokenResponseDto;
import com.pk.core.model.dto.response.UserResponseDto;
import com.pk.core.model.entity.PhoneOtpEntity;
import com.pk.core.model.entity.RoleEntity;
import com.pk.core.model.entity.UserEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;
import java.util.HexFormat;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class PhoneAuthServiceImpl implements PhoneAuthService {

    /** OTP có hiệu lực 5 phút. */
    static final int OTP_TTL_SECONDS = 300;
    /** Khoảng chờ tối thiểu giữa hai lần gửi OTP cho cùng SĐT + mục đích. */
    static final int RESEND_INTERVAL_SECONDS = 60;
    /** Số lần nhập sai tối đa trên một mã OTP. */
    static final int MAX_ATTEMPTS = 5;
    /** Registration token dùng được trong 15 phút sau khi verify OTP (khớp "expiresInSeconds" trả cho FE). */
    static final int REGISTRATION_TOKEN_TTL_SECONDS = 900;

    private final UserRepo users;
    private final RoleRepo roles;
    private final PhoneOtpRepo otps;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokens;
    private final OtpSender otpSender;

    private final SecureRandom random = new SecureRandom();

    /** Chỉ để dev/demo: có giá trị thì mọi OTP đều là mã này (đặt biến môi trường OTP_FIXED_CODE, 6 chữ
     * số). Để trống (mặc định) = sinh mã ngẫu nhiên. KHÔNG đặt ở môi trường thật. */
    @Value("${app.otp.fixed-code:}")
    private String fixedCode;

    @Transactional(readOnly = true)
    @Override
    public ResolvePhoneResponseDto resolvePhone(String phone) {
        UserEntity user = users.findByPhone(PhoneUtil.normalize(phone));
        if (user == null) {
            return new ResolvePhoneResponseDto(false, false, null);
        }
        boolean hasPassword = user.getPasswordHash() != null && !user.getPasswordHash().isBlank();
        return new ResolvePhoneResponseDto(true, hasPassword, maskEmail(user.getEmail()));
    }

    @Transactional
    @Override
    public MessageResponseDto sendRegisterOtp(String phone) {
        String normalized = PhoneUtil.normalize(phone);
        if (users.countByPhone(normalized) > 0) {
            throw BusinessException.conflict(ErrorCode.PHONE_TAKEN, "Số điện thoại đã được đăng ký");
        }
        issueOtp(normalized, PhoneOtpEntity.REGISTER);
        return MessageResponseDto.ok("Đã gửi mã OTP");
    }

    @Transactional(noRollbackFor = BusinessException.class)
    @Override
    public OtpVerificationResponseDto verifyRegisterOtp(VerifyOtpRequestDto req) {
        String phone = PhoneUtil.normalize(req.getPhone());
        PhoneOtpEntity otp = checkCode(phone, req.getOtp(), PhoneOtpEntity.REGISTER);
        String token = newRegistrationToken();
        otp.markVerified(sha256(token), plusSeconds(REGISTRATION_TOKEN_TTL_SECONDS));
        otp.touch();
        otps.update(otp);
        return new OtpVerificationResponseDto(token, REGISTRATION_TOKEN_TTL_SECONDS);
    }

    @Transactional
    @Override
    public TokenResponseDto completeRegistration(RegisterCompleteRequestDto req) {
        PhoneOtpEntity otp = otps.findByRegistrationTokenHash(sha256(req.getRegistrationToken()));
        if (otp == null || otp.isConsumed() || otp.isRegistrationExpired()) {
            throw BusinessException.badRequest(ErrorCode.REGISTRATION_TOKEN_INVALID,
                    "Phiên đăng ký không hợp lệ hoặc đã hết hạn");
        }
        String phone = otp.getPhone();
        if (users.countByPhone(phone) > 0) {
            throw BusinessException.conflict(ErrorCode.PHONE_TAKEN, "Số điện thoại đã được đăng ký");
        }
        String email = req.getEmail().trim().toLowerCase(Locale.ROOT);
        if (users.countByEmail(email) > 0) {
            throw BusinessException.conflict(ErrorCode.EMAIL_TAKEN, "Email đã được đăng ký");
        }
        UserEntity user = new UserEntity(phone, passwordEncoder.encode(req.getPassword()), req.getFullName().trim());
        user.setEmail(email);
        users.create(user);
        RoleEntity customer = roleOrCreate(RoleEntity.CUSTOMER);
        users.addRole(user.getId(), customer.getId());
        user.getRoles().add(customer);

        otp.consume();
        otp.touch();
        otps.update(otp);

        JwtProvider.AccessToken at = jwtProvider.issueAccessToken(user);
        return TokenResponseDto.bearer(at.token(), refreshTokens.issue(user), at.expiresInSeconds(),
                UserResponseDto.from(user));
    }

    @Transactional
    @Override
    public MessageResponseDto forgotPassword(String phone) {
        String normalized = PhoneUtil.normalize(phone);
        // SĐT chưa đăng ký: vẫn trả thành công, không gửi gì - để không lộ SĐT nào có tài khoản.
        if (users.countByPhone(normalized) > 0) {
            issueOtp(normalized, PhoneOtpEntity.RESET_PASSWORD);
        }
        return MessageResponseDto.ok("Nếu số điện thoại đã đăng ký, mã OTP đã được gửi");
    }

    @Transactional(noRollbackFor = BusinessException.class)
    @Override
    public MessageResponseDto resetPassword(ResetPasswordRequestDto req) {
        String phone = PhoneUtil.normalize(req.getPhone());
        UserEntity user = users.findByPhone(phone);
        if (user == null) {
            // Cùng lỗi với "sai OTP" để không lộ SĐT chưa đăng ký.
            throw BusinessException.badRequest(ErrorCode.OTP_INVALID, "Mã OTP không đúng hoặc đã được sử dụng");
        }
        PhoneOtpEntity otp = checkCode(phone, req.getOtp(), PhoneOtpEntity.RESET_PASSWORD);
        user.setPasswordHash(passwordEncoder.encode(req.getNewPassword()));
        user.touch();
        users.update(user);
        otp.consume();
        otp.touch();
        otps.update(otp);
        // Mật khẩu đổi thì mọi phiên cũ (refresh token) phải hết hiệu lực - phòng khi mật khẩu cũ đã bị lộ.
        refreshTokens.revokeAllForUser(user.getId());
        return MessageResponseDto.ok("Đã đặt lại mật khẩu");
    }

    /** Tạo OTP mới (có giới hạn tần suất) rồi gửi. */
    private void issueOtp(String phone, String purpose) {
        PhoneOtpEntity last = otps.findLatest(phone, purpose);
        if (last != null) {
            long elapsedMs = System.currentTimeMillis() - last.getCreatedDate().getTime();
            long waitSeconds = RESEND_INTERVAL_SECONDS - elapsedMs / 1000;
            if (waitSeconds > 0) {
                throw BusinessException.tooManyRequests(ErrorCode.OTP_RATE_LIMITED,
                        "Vui lòng đợi " + waitSeconds + " giây rồi gửi lại mã OTP", waitSeconds);
            }
        }
        String code = generateCode();
        otps.create(new PhoneOtpEntity(phone, purpose, hashCode(phone, code), plusSeconds(OTP_TTL_SECONDS)));
        otpSender.send(phone, code, purpose);
    }

    /**
     * Kiểm tra mã OTP mới nhất của SĐT + mục đích. Sai mã thì cộng số lần thử (nhớ gọi trong transaction
     * noRollbackFor BusinessException để số lần thử không bị rollback). Đúng thì trả về dòng OTP để caller
     * đánh dấu verified/consumed.
     */
    private PhoneOtpEntity checkCode(String phone, String code, String purpose) {
        PhoneOtpEntity otp = otps.findLatest(phone, purpose);
        if (otp == null || otp.isVerified() || otp.isConsumed()) {
            throw BusinessException.badRequest(ErrorCode.OTP_INVALID, "Mã OTP không đúng hoặc đã được sử dụng");
        }
        if (otp.isExpired()) {
            throw BusinessException.badRequest(ErrorCode.OTP_EXPIRED, "Mã OTP đã hết hạn");
        }
        if (otp.getAttempts() >= MAX_ATTEMPTS) {
            throw BusinessException.tooManyRequests(ErrorCode.OTP_TOO_MANY_ATTEMPTS,
                    "Bạn đã nhập sai quá nhiều lần, vui lòng yêu cầu mã mới");
        }
        boolean match = MessageDigest.isEqual(hashCode(phone, code).getBytes(StandardCharsets.UTF_8),
                otp.getCodeHash().getBytes(StandardCharsets.UTF_8));
        if (!match) {
            otp.setAttempts(otp.getAttempts() + 1);
            otp.touch();
            otps.update(otp);
            throw BusinessException.badRequest(ErrorCode.OTP_INVALID, "Mã OTP không đúng hoặc đã được sử dụng");
        }
        return otp;
    }

    private String generateCode() {
        if (fixedCode != null && fixedCode.matches("\\d{6}")) {
            return fixedCode;
        }
        return String.format("%06d", random.nextInt(1_000_000));
    }

    private String newRegistrationToken() {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    /** Gắn SĐT vào chuỗi băm để cùng một mã ở hai SĐT khác nhau cho hash khác nhau. */
    private static String hashCode(String phone, String code) {
        return sha256(phone + ":" + code);
    }

    private static String sha256(String value) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(md.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private static Date plusSeconds(int seconds) {
        return new Date(System.currentTimeMillis() + seconds * 1000L);
    }

    /** a***@gmail.com; null nếu không có email. */
    private static String maskEmail(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }
        int at = email.indexOf('@');
        if (at <= 0) {
            return null;
        }
        return email.charAt(0) + "***" + email.substring(at);
    }

    /** Role thường đã có từ migration V1; tạo lại nếu thiếu (vd. DB test rỗng). */
    private RoleEntity roleOrCreate(String name) {
        RoleEntity role = roles.findByName(name);
        return role != null ? role : roles.create(new RoleEntity(name));
    }
}
