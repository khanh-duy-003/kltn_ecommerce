package com.pk.core.business.repository;

import com.pk.core.model.entity.RefreshTokenEntity;

import org.springframework.data.repository.query.Param;

import vn.com.unit.springframework.data.mirage.repository.query.Modifying;

import java.util.Date;

public interface RefreshTokenRepo extends PkRepo<RefreshTokenEntity, Long> {

    /** Trả null nếu không có. */
    RefreshTokenEntity findByTokenHash(@Param("tokenHash") String tokenHash);

    /** Thu hồi mọi token chưa thu hồi của một family; trả về số dòng bị ảnh hưởng. */
    @Modifying
    int revokeFamily(@Param("familyId") String familyId, @Param("now") Date now);
}
