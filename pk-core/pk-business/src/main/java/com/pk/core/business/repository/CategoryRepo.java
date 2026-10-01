package com.pk.core.business.repository;

import com.pk.core.model.entity.CategoryEntity;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepo extends PkRepo<CategoryEntity, Long> {

    /** Trả null nếu không có. */
    CategoryEntity findBySlug(@Param("slug") String slug);

    List<CategoryEntity> findAllActive();
}
