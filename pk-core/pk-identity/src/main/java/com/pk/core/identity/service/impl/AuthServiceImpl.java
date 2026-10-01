package com.pk.core.identity.service.impl;

import lombok.RequiredArgsConstructor;

import com.pk.core.common.exception.ErrorCode;
import com.pk.core.identity.service.AuthService;
import com.pk.core.identity.service.RefreshTokenService;

import com.pk.core.common.exception.BusinessException;
import com.pk.core.common.exception.ResourceNotFoundException;
import com.pk.core.model.dto.request.LoginRequestDto;
import com.pk.core.model.dto.request.RegisterRequestDto;
import com.pk.core.model.dto.request.UpdateProfileRequestDto;
import com.pk.core.model.dto.response.TokenResponseDto;
import com.pk.core.model.dto.response.UserResponseDto;
import com.pk.core.identity.security.jwt.JwtProvider;
import com.pk.core.model.entity.RoleEntity;
import com.pk.core.business.repository.RoleRepo;
import com.pk.core.model.entity.UserEntity;
import com.pk.core.business.repository.UserRepo;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepo users;
    private final RoleRepo roles;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokens;


    @Transactional
    @Override
    public TokenResponseDto register(RegisterRequestDto req) {
        String email = normalize(req.getEmail());
        if (users.countByEmail(email) > 0) {
            throw BusinessException.conflict(ErrorCode.EMAIL_TAKEN, "Email đã được đăng ký");
        }
        UserEntity user = new UserEntity(email, passwordEncoder.encode(req.getPassword()),
                req.getFullName().trim(), req.getPhone());
        users.create(user);
        RoleEntity customer = roleOrCreate(RoleEntity.CUSTOMER);
        users.addRole(user.getId(), customer.getId());
        user.getRoles().add(customer);
        return tokensFor(user, refreshTokens.issue(user));
    }

    @Transactional
    @Override
    public TokenResponseDto login(LoginRequestDto req) {
        UserEntity user = UserRoles.attach(users.findByEmail(normalize(req.getEmail())), roles);
        // Cùng một thông báo cho "sai email" và "sai mật khẩu" để không lộ email nào đã đăng ký.
        if (user == null || !passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw BusinessException.unauthorized(ErrorCode.INVALID_CREDENTIALS, "Email hoặc mật khẩu không đúng");
        }
        if (!user.isEnabled()) {
            throw BusinessException.unauthorized(ErrorCode.ACCOUNT_DISABLED, "Tài khoản đã bị khoá");
        }
        return tokensFor(user, refreshTokens.issue(user));
    }

    @Transactional
    @Override
    public TokenResponseDto loginAdmin(LoginRequestDto req) {
        UserEntity user = UserRoles.attach(users.findByEmail(normalize(req.getEmail())), roles);
        // Cùng thông báo cho "sai email"/"sai mật khẩu" như login() thường - không lộ email đã đăng ký.
        if (user == null || !passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw BusinessException.unauthorized(ErrorCode.INVALID_CREDENTIALS, "Email hoặc mật khẩu không đúng");
        }
        if (!user.isEnabled()) {
            throw BusinessException.unauthorized(ErrorCode.ACCOUNT_DISABLED, "Tài khoản đã bị khoá");
        }
        // Khác login() thường: bắt buộc có role ADMIN, sai thì 403 (đã xác thực đúng danh tính, chỉ
        // không đủ quyền - không dùng lại INVALID_CREDENTIALS vì đó là lỗi 401 sai danh tính).
        if (!user.hasRole(RoleEntity.ADMIN)) {
            throw BusinessException.forbidden(ErrorCode.FORBIDDEN, "Tài khoản không có quyền quản trị");
        }
        return tokensFor(user, refreshTokens.issue(user));
    }

    @Transactional(noRollbackFor = BusinessException.class)
    @Override
    public TokenResponseDto refresh(String refreshToken) {
        RefreshTokenService.Rotation r = refreshTokens.rotate(refreshToken);
        return tokensFor(r.user(), r.newRefreshToken());
    }

    @Transactional
    @Override
    public void logout(String refreshToken) {
        refreshTokens.revoke(refreshToken);
    }

    @Transactional(readOnly = true)
    @Override
    public UserResponseDto me(Long userId) {
        UserEntity user = UserRoles.attach(users.findOne(userId), roles);
        if (user == null) {
            throw new ResourceNotFoundException("Người dùng", "User", userId);
        }
        return UserResponseDto.from(user);
    }

    @Transactional
    @Override
    public UserResponseDto updateProfile(Long userId, UpdateProfileRequestDto req) {
        UserEntity user = UserRoles.attach(users.findOne(userId), roles);
        if (user == null) {
            throw new ResourceNotFoundException("Người dùng", "User", userId);
        }
        String email = normalize(req.getEmail());
        if (!email.equals(user.getEmail()) && users.countByEmail(email) > 0) {
            throw BusinessException.conflict(ErrorCode.EMAIL_TAKEN, "Email đã được đăng ký");
        }
        user.setFullName(req.getFullName().trim());
        user.setEmail(email);
        user.touch();
        users.update(user);
        return UserResponseDto.from(user);
    }

    private TokenResponseDto tokensFor(UserEntity user, String refreshToken) {
        JwtProvider.AccessToken at = jwtProvider.issueAccessToken(user);
        return TokenResponseDto.bearer(at.token(), refreshToken, at.expiresInSeconds(), UserResponseDto.from(user));
    }

    /** Role thường đã có từ migration V1; tạo lại nếu thiếu (vd. DB test rỗng). */
    private RoleEntity roleOrCreate(String name) {
        RoleEntity role = roles.findByName(name);
        return role != null ? role : roles.create(new RoleEntity(name));
    }

    private static String normalize(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

}
