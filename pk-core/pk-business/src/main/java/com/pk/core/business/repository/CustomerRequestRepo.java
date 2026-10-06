package com.pk.core.business.repository;

import com.pk.core.model.entity.CustomerRequestEntity;
import org.springframework.data.repository.query.Param;

public interface CustomerRequestRepo extends PkRepo<CustomerRequestEntity, Long> {

    /**
     * Yêu cầu CHƯA hoàn tất (status khác COMPLETED) trùng loại + kênh liên hệ + SKU + mã đơn - dùng chống gửi trùng.
     * Không áp dụng thì truyền giá trị trung tính: skuId = 0, orderCode = "". Trả null nếu không có.
     */
    CustomerRequestEntity findOpen(@Param("type") String type, @Param("contactValue") String contactValue,
                                   @Param("skuId") Long skuId, @Param("orderCode") String orderCode);
}
