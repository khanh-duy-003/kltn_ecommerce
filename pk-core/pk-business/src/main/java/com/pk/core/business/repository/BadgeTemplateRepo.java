package com.pk.core.business.repository;

import com.pk.core.model.entity.BadgeTemplateEntity;
import org.springframework.data.repository.query.Param;

public interface BadgeTemplateRepo extends PkRepo<BadgeTemplateEntity, Long> {

    /** Trả null nếu không có - dùng kiểm tra trùng `code` (mẫu đã xoá mềm đã bị đổi code nên không trùng). */
    BadgeTemplateEntity findByCode(@Param("code") String code);
}
