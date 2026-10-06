package com.pk.core.identity.service;

import com.pk.core.model.dto.request.RegisterCompleteRequestDto;
import com.pk.core.model.dto.request.ResetPasswordRequestDto;
import com.pk.core.model.dto.request.VerifyOtpRequestDto;
import com.pk.core.model.dto.response.MessageResponseDto;
import com.pk.core.model.dto.response.OtpVerificationResponseDto;
import com.pk.core.model.dto.response.ResolvePhoneResponseDto;
import com.pk.core.model.dto.response.TokenResponseDto;

/**
 * Luồng SĐT + OTP theo spec FE (/storefront/auth/...): resolve-phone, đăng ký 3 bước (send-otp ->
 * verify-otp -> complete), quên/đặt lại mật khẩu. Tách khỏi {@link AuthService} (đăng ký/đăng nhập cũ)
 * để không đổi hành vi các endpoint đã có. Mọi SĐT đầu vào được chuẩn hoá bằng PhoneUtil.normalize.
 */
public interface PhoneAuthService {

    /** SĐT đã có tài khoản chưa (và email đã che nếu có). */
    ResolvePhoneResponseDto resolvePhone(String phone);

    /** Gửi OTP đăng ký. 409 nếu SĐT đã đăng ký; 429 nếu gửi lại quá sớm. */
    MessageResponseDto sendRegisterOtp(String phone);

    /** Kiểm tra OTP đăng ký, trả registration token dùng một lần cho {@link #completeRegistration}. */
    OtpVerificationResponseDto verifyRegisterOtp(VerifyOtpRequestDto req);

    /** Tạo tài khoản CUSTOMER từ registration token, trả token đăng nhập như login thường. */
    TokenResponseDto completeRegistration(RegisterCompleteRequestDto req);

    /** Gửi OTP quên mật khẩu. Luôn trả thành công kể cả SĐT chưa đăng ký (không lộ SĐT nào có tài khoản). */
    MessageResponseDto forgotPassword(String phone);

    /** Kiểm tra OTP quên mật khẩu rồi đặt mật khẩu mới. */
    MessageResponseDto resetPassword(ResetPasswordRequestDto req);
}
