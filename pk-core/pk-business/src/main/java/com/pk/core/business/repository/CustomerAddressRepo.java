package com.pk.core.business.repository;

import com.pk.core.model.entity.CustomerAddressEntity;
import org.springframework.data.repository.query.Param;
import vn.com.unit.springframework.data.mirage.repository.query.Modifying;

import java.util.List;

public interface CustomerAddressRepo extends PkRepo<CustomerAddressEntity, Long> {

    List<CustomerAddressEntity> findByUserId(@Param("userId") Long userId);

    /** Trả null nếu không có hoặc không thuộc user này - tránh lộ/sửa địa chỉ của người khác (IDOR). */
    CustomerAddressEntity findByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

    /** Bỏ cờ mặc định của mọi địa chỉ khác thuộc user trước khi đặt địa chỉ mới làm mặc định. */
    @Modifying
    int clearDefaultForUser(@Param("userId") Long userId);
}
