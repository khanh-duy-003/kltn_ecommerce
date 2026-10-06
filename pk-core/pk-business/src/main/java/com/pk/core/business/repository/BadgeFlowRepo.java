package com.pk.core.business.repository;

import com.pk.core.model.entity.BadgeFlowEntity;

/** Chỉ cần CRUD cơ bản + findAll (lọc/sắp ở service) nên không có truy vấn riêng. */
public interface BadgeFlowRepo extends PkRepo<BadgeFlowEntity, Long> {
}
