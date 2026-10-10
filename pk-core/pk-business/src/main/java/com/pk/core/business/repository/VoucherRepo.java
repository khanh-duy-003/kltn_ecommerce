package com.pk.core.business.repository;

import com.pk.core.model.entity.VoucherEntity;
import org.springframework.data.repository.query.Param;
import vn.com.unit.springframework.data.mirage.repository.query.Modifying;

public interface VoucherRepo extends PkRepo<VoucherEntity, Long> {

    /** Trả null nếu không có. */
    VoucherEntity findByCode(@Param("code") String code);

    /** Tăng used_count NGUYÊN TỬ, chỉ khi còn lượt (usage_limit IS NULL hoặc used_count < usage_limit)
     * - tránh race condition khi nhiều request cùng dùng 1 voucher gần hết lượt. Trả 0 nếu vừa hết
     * lượt (đối chiếu lúc VoucherServiceImpl.validate() còn lượt nhưng có request khác dùng trước). */
    @Modifying
    int incrementUsage(@Param("id") Long id);

    /** Trả lại 1 lượt dùng (huỷ đơn có áp voucher). Không để used_count âm. */
    @Modifying
    int decrementUsage(@Param("id") Long id);
}
