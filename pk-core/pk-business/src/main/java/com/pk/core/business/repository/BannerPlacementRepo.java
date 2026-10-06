package com.pk.core.business.repository;

import com.pk.core.model.entity.BannerPlacementEntity;
import org.springframework.data.repository.query.Param;

public interface BannerPlacementRepo extends PkRepo<BannerPlacementEntity, Long> {

    /** Trả null nếu không có vị trí với mã này. */
    BannerPlacementEntity findByCode(@Param("code") String code);
}
