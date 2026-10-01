package com.pk.core.identity.service.impl;

import com.pk.core.model.entity.UserEntity;
import com.pk.core.business.repository.RoleRepo;

/** Nạp role của user (Mirage không có quan hệ tự động như JPA). */
final class UserRoles {

    private UserRoles() {
    }

    static UserEntity attach(UserEntity user, RoleRepo roles) {
        if (user != null && user.getId() != null) {
            user.getRoles().clear();
            user.getRoles().addAll(roles.findByUserId(user.getId()));
        }
        return user;
    }
}
