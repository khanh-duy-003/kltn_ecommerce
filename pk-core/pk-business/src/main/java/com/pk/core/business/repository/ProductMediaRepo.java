package com.pk.core.business.repository;

import com.pk.core.model.entity.ProductMediaEntity;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductMediaRepo extends PkRepo<ProductMediaEntity, Long> {

    /** Theo sort_order, rồi id. */
    List<ProductMediaEntity> findByProductId(@Param("productId") Long productId);
}
