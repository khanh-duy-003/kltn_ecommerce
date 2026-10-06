package com.pk.core.business.repository;

import com.pk.core.model.entity.PreOrderConfigEntity;

/** Chỉ cần create + findAll (service tự sắp mới nhất trước) nên không có truy vấn riêng. */
public interface PreOrderConfigRepo extends PkRepo<PreOrderConfigEntity, Long> {
}
