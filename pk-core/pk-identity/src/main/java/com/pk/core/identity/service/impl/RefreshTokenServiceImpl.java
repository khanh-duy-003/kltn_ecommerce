package com.pk.core.identity.service.impl;

import lombok.RequiredArgsConstructor;

import com.pk.core.common.exception.ErrorCode;
import com.pk.core.model.entity.RefreshTokenEntity;
import com.pk.core.business.repository.RefreshTokenRepo;
import com.pk.core.business.repository.RoleRepo;
import com.pk.core.business.repository.UserRepo;
import com.pk.core.identity.service.RefreshTokenService;

import com.pk.core.common.exception.BusinessException;
import com.pk.core.identity.config.JwtProperties;
import com.pk.core.model.entity.UserEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.HexFormat;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final RefreshTokenRepo repository;
    private final UserRepo users;
    private final RoleRepo roles;
    private final JwtProperties props;


    /** Đăng nhập mới => family mới. */
    @Transactional
    @Override
    public String issue(UserEntity user) {
        return create(user, UUID.randomUUID().toString());
    }

    /**
     * noRollbackFor: khi phát hiện dùng lại token cũ ta thu hồi cả family rồi ném lỗi;
     * nếu rollback thì việc thu hồi bị huỷ mất.
     */
    @Transactional(noRollbackFor = BusinessException.class)
    @Override
    public Rotation rotate(String rawToken) {
        RefreshTokenEntity current = repository.findByTokenHash(hash(rawToken));
        if (current == null) {
            throw BusinessException.unauthorized(ErrorCode.REFRESH_INVALID, "Refresh token không hợp lệ");
        }

        if (current.isRevoked()) {
            repository.revokeFamily(current.getFamilyId(), new Date());
            throw BusinessException.unauthorized(ErrorCode.REFRESH_REUSED, "Refresh token đã được sử dụng, vui lòng đăng nhập lại");
        }
        if (current.isExpired()) {
            throw BusinessException.unauthorized(ErrorCode.REFRESH_EXPIRED, "Refresh token đã hết hạn");
        }
        current.revoke();
        current.touch();
        repository.update(current);
        UserEntity user = UserRoles.attach(users.findOne(current.getUserId()), roles);
        if (user == null || !user.isEnabled()) {
            throw BusinessException.unauthorized(ErrorCode.ACCOUNT_DISABLED, "Tài khoản đã bị khoá");
        }
        return new Rotation(user, create(user, current.getFamilyId()));
    }

    /** Đăng xuất: thu hồi cả family của token này. Token lạ/đã thu hồi thì bỏ qua (idempotent). */
    @Transactional
    @Override
    public void revoke(String rawToken) {
        RefreshTokenEntity t = repository.findByTokenHash(hash(rawToken));
        if (t != null) {
            repository.revokeFamily(t.getFamilyId(), new Date());
        }
    }

    private String create(UserEntity user, String familyId) {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        String raw = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        Date expires = Date.from(Instant.now().plus(props.getRefreshTokenDays(), ChronoUnit.DAYS));
        repository.create(new RefreshTokenEntity(user.getId(), hash(raw), familyId, expires));
        return raw;
    }

    static String hash(String raw) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(md.digest(raw.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
