package com.pk.core.business.repository;

import com.pk.core.model.entity.SkuAttributeValueEntity;
import org.springframework.data.repository.query.Param;
import vn.com.unit.springframework.data.mirage.repository.query.Modifying;

import java.util.List;

public interface SkuAttributeValueRepo extends PkRepo<SkuAttributeValueEntity, Long> {

    List<SkuAttributeValueEntity> findBySkuId(@Param("skuId") Long skuId);

    /** Toàn bộ giá trị thuộc tính của mọi SKU thuộc 1 sản phẩm (1 truy vấn thay vì N+1). */
    List<SkuAttributeValueEntity> findByProductId(@Param("productId") Long productId);

    @Modifying
    int deleteBySkuId(@Param("skuId") Long skuId);
}
