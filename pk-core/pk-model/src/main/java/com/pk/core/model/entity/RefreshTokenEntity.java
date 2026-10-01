package com.pk.core.model.entity;

import com.pk.core.model.constant.TableConstant;
import com.pk.core.common.entity.UpdateEntity;
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
 * Refresh token dạng chuỗi ngẫu nhiên; DB chỉ lưu SHA-256 (lộ DB không lộ token).
 * Mỗi lần refresh sinh token mới cùng familyId; dùng lại token cũ => thu hồi cả family.
 */
@Getter
@Setter
@NoArgsConstructor
@Table(name = TableConstant.REFRESH_TOKENS)
public class RefreshTokenEntity extends UpdateEntity {

    @Id
    @PrimaryKey(generationType = GenerationType.SEQUENCE, generator = "refresh_tokens_id_seq")
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "token_hash")
    private String tokenHash;

    @Column(name = "family_id")
    private String familyId;

    @Column(name = "expires_at")
    private Date expiresAt;

    @Column(name = "revoked_at")
    private Date revokedAt;

    public RefreshTokenEntity(Long userId, String tokenHash, String familyId, Date expiresAt) {
        this.userId = userId;
        this.tokenHash = tokenHash;
        this.familyId = familyId;
        this.expiresAt = expiresAt;
    }

    public boolean isRevoked() { return revokedAt != null; }

    public boolean isExpired() { return expiresAt.before(new Date()); }

    public void revoke() { if (revokedAt == null) revokedAt = new Date(); }
}
