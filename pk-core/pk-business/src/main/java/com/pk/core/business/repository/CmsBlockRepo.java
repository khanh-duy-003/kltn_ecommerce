package com.pk.core.business.repository;

import com.pk.core.model.entity.CmsBlockEntity;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CmsBlockRepo extends PkRepo<CmsBlockEntity, Long> {

    /** Sắp theo sort_order tăng dần - thứ tự hiển thị block trên trang. */
    List<CmsBlockEntity> findByPageId(@Param("pageId") Long pageId);
}
