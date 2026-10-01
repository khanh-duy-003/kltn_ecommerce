package com.pk.core.business.repository;

import com.pk.core.model.entity.ProductAttributeEntity;
import org.springframework.data.repository.query.Param;

public interface ProductAttributeRepo extends PkRepo<ProductAttributeEntity, Long> {

    /** Trả null nếu không có - dùng kiểm tra trùng `code` (409 DUPLICATE_CODE). */
    ProductAttributeEntity findByCode(@Param("code") String code);
}
