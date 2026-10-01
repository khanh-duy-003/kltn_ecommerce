package com.pk.core.business.repository;

import com.pk.core.model.entity.RoleEntity;

import org.springframework.data.repository.query.Param;


import java.util.List;

public interface RoleRepo extends PkRepo<RoleEntity, Long> {

    /** Trả null nếu không có. */
    RoleEntity findByName(@Param("name") String name);

    List<RoleEntity> findByUserId(@Param("userId") Long userId);
}
