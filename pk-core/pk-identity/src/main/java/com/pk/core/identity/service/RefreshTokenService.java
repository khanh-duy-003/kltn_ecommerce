package com.pk.core.identity.service;

import com.pk.core.model.entity.UserEntity;

public interface RefreshTokenService {

    /** Kết quả xoay vòng: user sở hữu token + refresh token mới (dạng thô, chỉ trả cho client 1 lần). */
    record Rotation(UserEntity user, String newRefreshToken) {
    }

    /** Đăng nhập mới => family mới. */
    String issue(UserEntity user);

    /** Xoay vòng refresh token; phát hiện dùng lại token cũ thì thu hồi cả family. */
    Rotation rotate(String rawToken);

    void revoke(String rawToken);
}
