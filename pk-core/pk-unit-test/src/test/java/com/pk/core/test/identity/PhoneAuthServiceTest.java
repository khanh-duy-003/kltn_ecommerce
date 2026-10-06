package com.pk.core.test.identity;

import com.pk.core.business.repository.PhoneOtpRepo;
import com.pk.core.business.repository.RoleRepo;
import com.pk.core.business.repository.UserRepo;
import com.pk.core.common.exception.BusinessException;
import com.pk.core.identity.security.jwt.JwtProvider;
import com.pk.core.identity.service.OtpSender;
import com.pk.core.identity.service.PhoneAuthService;
import com.pk.core.identity.service.RefreshTokenService;
import com.pk.core.identity.service.impl.PhoneAuthServiceImpl;
import com.pk.core.model.dto.request.RegisterCompleteRequestDto;
import com.pk.core.model.dto.request.ResetPasswordRequestDto;
import com.pk.core.model.dto.request.VerifyOtpRequestDto;
import com.pk.core.model.dto.response.MessageResponseDto;
import com.pk.core.model.dto.response.OtpVerificationResponseDto;
import com.pk.core.model.dto.response.ResolvePhoneResponseDto;
import com.pk.core.model.dto.response.TokenResponseDto;
import com.pk.core.model.entity.PhoneOtpEntity;
import com.pk.core.model.entity.RoleEntity;
import com.pk.core.model.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PhoneAuthServiceTest {

    private static final String PHONE = "0901234567";

    @Mock UserRepo users;
    @Mock RoleRepo roles;
    @Mock PhoneOtpRepo otps;
    @Mock PasswordEncoder encoder;
    @Mock JwtProvider jwtProvider;
    @Mock RefreshTokenService refreshTokens;
    @Mock OtpSender otpSender;

    private PhoneAuthService service;
    private String lastCode;

    @BeforeEach
    void setUp() {
        service = new PhoneAuthServiceImpl(users, roles, otps, encoder, jwtProvider, refreshTokens, otpSender);
    }

    /** Gửi OTP đăng ký qua service thật, trả về dòng OTP đã tạo; lastCode giữ mã đã "gửi". */
    private PhoneOtpEntity sendRegisterOtp() {
        when(users.countByPhone(PHONE)).thenReturn(0L);
        when(otps.findLatest(PHONE, PhoneOtpEntity.REGISTER)).thenReturn(null);
        service.sendRegisterOtp(PHONE);
        ArgumentCaptor<PhoneOtpEntity> otpCaptor = ArgumentCaptor.forClass(PhoneOtpEntity.class);
        ArgumentCaptor<String> codeCaptor = ArgumentCaptor.forClass(String.class);
        verify(otps).create(otpCaptor.capture());
        verify(otpSender).send(eq(PHONE), codeCaptor.capture(), eq(PhoneOtpEntity.REGISTER));
        lastCode = codeCaptor.getValue();
        return otpCaptor.getValue();
    }

    private String wrongCode() {
        return "000000".equals(lastCode) ? "111111" : "000000";
    }

    @Test
    void resolvePhoneUnknownPhone() {
        when(users.findByPhone(PHONE)).thenReturn(null);

        ResolvePhoneResponseDto res = service.resolvePhone(PHONE);

        assertFalse(res.isExists());
        assertFalse(res.isHasPassword());
        assertNull(res.getMaskedEmail());
    }

    @Test
    void resolvePhoneKnownPhoneMasksEmail() {
        UserEntity u = new UserEntity(PHONE, "hashed", "An");
        u.setEmail("lan.nguyen@gmail.com");
        when(users.findByPhone("0901234567")).thenReturn(u);

        ResolvePhoneResponseDto res = service.resolvePhone("+84901234567");

        assertTrue(res.isExists());
        assertTrue(res.isHasPassword());
        assertEquals("l***@gmail.com", res.getMaskedEmail());
    }

    @Test
    void sendRegisterOtpRejectsExistingPhone() {
        when(users.countByPhone(PHONE)).thenReturn(1L);

        BusinessException ex = assertThrows(BusinessException.class, () -> service.sendRegisterOtp(PHONE));

        assertEquals("PHONE_TAKEN", ex.getCode());
        assertEquals(409, ex.getStatus());
        verify(otps, never()).create(any());
        verify(otpSender, never()).send(anyString(), anyString(), anyString());
    }

    @Test
    void sendRegisterOtpStoresHashedCodeAndSendsPlainCode() {
        PhoneOtpEntity otp = sendRegisterOtp();

        assertTrue(lastCode.matches("\\d{6}"));
        assertEquals(PHONE, otp.getPhone());
        assertEquals(PhoneOtpEntity.REGISTER, otp.getPurpose());
        assertNotEquals(lastCode, otp.getCodeHash());
        assertEquals(64, otp.getCodeHash().length());
        assertTrue(otp.getExpiresAt().after(new Date()));
        assertEquals(0, otp.getAttempts());
    }

    @Test
    void sendRegisterOtpTooSoonIsRateLimited() {
        PhoneOtpEntity recent = new PhoneOtpEntity(PHONE, PhoneOtpEntity.REGISTER, "h", new Date(System.currentTimeMillis() + 300_000));
        when(users.countByPhone(PHONE)).thenReturn(0L);
        when(otps.findLatest(PHONE, PhoneOtpEntity.REGISTER)).thenReturn(recent);

        BusinessException ex = assertThrows(BusinessException.class, () -> service.sendRegisterOtp(PHONE));

        assertEquals("OTP_RATE_LIMITED", ex.getCode());
        assertEquals(429, ex.getStatus());
        verify(otps, never()).create(any());
    }

    @Test
    void verifyWrongCodeCountsAttempt() {
        PhoneOtpEntity otp = sendRegisterOtp();
        when(otps.findLatest(PHONE, PhoneOtpEntity.REGISTER)).thenReturn(otp);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.verifyRegisterOtp(new VerifyOtpRequestDto(PHONE, wrongCode())));

        assertEquals("OTP_INVALID", ex.getCode());
        assertEquals(400, ex.getStatus());
        assertEquals(1, otp.getAttempts());
        verify(otps).update(otp);
    }

    @Test
    void verifyIsLockedAfterMaxAttempts() {
        PhoneOtpEntity otp = new PhoneOtpEntity(PHONE, PhoneOtpEntity.REGISTER, "h", new Date(System.currentTimeMillis() + 300_000));
        otp.setAttempts(5);
        when(otps.findLatest(PHONE, PhoneOtpEntity.REGISTER)).thenReturn(otp);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.verifyRegisterOtp(new VerifyOtpRequestDto(PHONE, "123456")));

        assertEquals("OTP_TOO_MANY_ATTEMPTS", ex.getCode());
        assertEquals(429, ex.getStatus());
        verify(otps, never()).update(any());
    }

    @Test
    void verifyExpiredCode() {
        PhoneOtpEntity otp = new PhoneOtpEntity(PHONE, PhoneOtpEntity.REGISTER, "h", new Date(System.currentTimeMillis() - 1000));
        when(otps.findLatest(PHONE, PhoneOtpEntity.REGISTER)).thenReturn(otp);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.verifyRegisterOtp(new VerifyOtpRequestDto(PHONE, "123456")));

        assertEquals("OTP_EXPIRED", ex.getCode());
    }

    @Test
    void verifyWithNoOtpIsInvalid() {
        when(otps.findLatest(PHONE, PhoneOtpEntity.REGISTER)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.verifyRegisterOtp(new VerifyOtpRequestDto(PHONE, "123456")));

        assertEquals("OTP_INVALID", ex.getCode());
    }

    @Test
    void verifyCorrectCodeReturnsRegistrationTokenAndCannotBeReused() {
        PhoneOtpEntity otp = sendRegisterOtp();
        when(otps.findLatest(PHONE, PhoneOtpEntity.REGISTER)).thenReturn(otp);

        OtpVerificationResponseDto res = service.verifyRegisterOtp(new VerifyOtpRequestDto(PHONE, lastCode));

        assertNotNull(res.getRegistrationToken());
        assertEquals(900, res.getExpiresInSeconds());
        assertTrue(otp.isVerified());
        assertNotEquals(res.getRegistrationToken(), otp.getRegistrationTokenHash());
        verify(otps).update(otp);

        // dùng lại cùng mã OTP lần nữa: không được phép
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.verifyRegisterOtp(new VerifyOtpRequestDto(PHONE, lastCode)));
        assertEquals("OTP_INVALID", ex.getCode());
    }

    @Test
    void completeRegistrationCreatesCustomerAndConsumesToken() {
        PhoneOtpEntity otp = sendRegisterOtp();
        when(otps.findLatest(PHONE, PhoneOtpEntity.REGISTER)).thenReturn(otp);
        String token = service.verifyRegisterOtp(new VerifyOtpRequestDto(PHONE, lastCode)).getRegistrationToken();

        when(otps.findByRegistrationTokenHash(anyString())).thenReturn(otp);
        when(users.countByPhone(PHONE)).thenReturn(0L);
        when(users.countByEmail("an@test.local")).thenReturn(0L);
        when(encoder.encode("password1")).thenReturn("hashed");
        when(roles.findByName(RoleEntity.CUSTOMER)).thenReturn(new RoleEntity(RoleEntity.CUSTOMER));
        when(jwtProvider.issueAccessToken(any())).thenReturn(new JwtProvider.AccessToken("at", 900));
        when(refreshTokens.issue(any())).thenReturn("rt");

        TokenResponseDto res = service.completeRegistration(
                new RegisterCompleteRequestDto(token, " An ", "AN@Test.local", "password1"));

        assertEquals("at", res.getAccessToken());
        assertEquals("rt", res.getRefreshToken());
        assertEquals(PHONE, res.getUser().getPhone());
        assertEquals("an@test.local", res.getUser().getEmail());
        assertEquals("An", res.getUser().getFullName());
        assertTrue(otp.isConsumed());
        ArgumentCaptor<UserEntity> userCaptor = ArgumentCaptor.forClass(UserEntity.class);
        verify(users).create(userCaptor.capture());
        assertEquals("hashed", userCaptor.getValue().getPasswordHash());
    }

    @Test
    void completeRegistrationRejectsUnknownToken() {
        when(otps.findByRegistrationTokenHash(anyString())).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class, () -> service.completeRegistration(
                new RegisterCompleteRequestDto("nope", "An", "an@test.local", "password1")));

        assertEquals("REGISTRATION_TOKEN_INVALID", ex.getCode());
        assertEquals(400, ex.getStatus());
        verify(users, never()).create(any());
    }

    @Test
    void completeRegistrationRejectsExpiredToken() {
        PhoneOtpEntity otp = new PhoneOtpEntity(PHONE, PhoneOtpEntity.REGISTER, "h", new Date());
        otp.markVerified("x", new Date(System.currentTimeMillis() - 1000));
        when(otps.findByRegistrationTokenHash(anyString())).thenReturn(otp);

        BusinessException ex = assertThrows(BusinessException.class, () -> service.completeRegistration(
                new RegisterCompleteRequestDto("tok", "An", "an@test.local", "password1")));

        assertEquals("REGISTRATION_TOKEN_INVALID", ex.getCode());
    }

    @Test
    void completeRegistrationRejectsTakenEmail() {
        PhoneOtpEntity otp = new PhoneOtpEntity(PHONE, PhoneOtpEntity.REGISTER, "h", new Date());
        otp.markVerified("x", new Date(System.currentTimeMillis() + 60_000));
        when(otps.findByRegistrationTokenHash(anyString())).thenReturn(otp);
        when(users.countByPhone(PHONE)).thenReturn(0L);
        when(users.countByEmail("an@test.local")).thenReturn(1L);

        BusinessException ex = assertThrows(BusinessException.class, () -> service.completeRegistration(
                new RegisterCompleteRequestDto("tok", "An", "an@test.local", "password1")));

        assertEquals("EMAIL_TAKEN", ex.getCode());
        assertEquals(409, ex.getStatus());
        verify(users, never()).create(any());
    }

    @Test
    void forgotPasswordForUnknownPhoneSucceedsWithoutSending() {
        when(users.countByPhone(PHONE)).thenReturn(0L);

        MessageResponseDto res = service.forgotPassword(PHONE);

        assertTrue(res.isSuccess());
        verify(otps, never()).create(any());
        verify(otpSender, never()).send(anyString(), anyString(), anyString());
    }

    @Test
    void resetPasswordWithCorrectOtpChangesPasswordAndConsumesOtp() {
        when(users.countByPhone(PHONE)).thenReturn(1L);
        when(otps.findLatest(PHONE, PhoneOtpEntity.RESET_PASSWORD)).thenReturn(null);
        service.forgotPassword(PHONE);
        ArgumentCaptor<PhoneOtpEntity> otpCaptor = ArgumentCaptor.forClass(PhoneOtpEntity.class);
        ArgumentCaptor<String> codeCaptor = ArgumentCaptor.forClass(String.class);
        verify(otps).create(otpCaptor.capture());
        verify(otpSender).send(eq(PHONE), codeCaptor.capture(), eq(PhoneOtpEntity.RESET_PASSWORD));
        PhoneOtpEntity otp = otpCaptor.getValue();

        UserEntity user = new UserEntity(PHONE, "old", "An");
        user.setId(5L);
        when(users.findByPhone(PHONE)).thenReturn(user);
        when(otps.findLatest(PHONE, PhoneOtpEntity.RESET_PASSWORD)).thenReturn(otp);
        when(encoder.encode("newpassword1")).thenReturn("newhash");

        MessageResponseDto res = service.resetPassword(new ResetPasswordRequestDto(PHONE, codeCaptor.getValue(), "newpassword1"));

        assertTrue(res.isSuccess());
        assertEquals("newhash", user.getPasswordHash());
        assertTrue(otp.isConsumed());
        verify(users).update(user);
        verify(refreshTokens).revokeAllForUser(5L);
    }

    @Test
    void resetPasswordForUnknownPhoneLooksLikeWrongOtp() {
        when(users.findByPhone(PHONE)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.resetPassword(new ResetPasswordRequestDto(PHONE, "123456", "newpassword1")));

        assertEquals("OTP_INVALID", ex.getCode());
        verify(users, never()).update(any());
    }

    @Test
    void resetPasswordWithWrongOtpKeepsPassword() {
        UserEntity user = new UserEntity(PHONE, "old", "An");
        PhoneOtpEntity otp = new PhoneOtpEntity(PHONE, PhoneOtpEntity.RESET_PASSWORD, "not-the-real-hash", new Date(System.currentTimeMillis() + 300_000));
        when(users.findByPhone(PHONE)).thenReturn(user);
        when(otps.findLatest(PHONE, PhoneOtpEntity.RESET_PASSWORD)).thenReturn(otp);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.resetPassword(new ResetPasswordRequestDto(PHONE, "123456", "newpassword1")));

        assertEquals("OTP_INVALID", ex.getCode());
        assertEquals("old", user.getPasswordHash());
        assertEquals(1, otp.getAttempts());
        verify(users, never()).update(any());
    }
}
