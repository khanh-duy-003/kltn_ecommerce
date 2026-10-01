package com.pk.core.business.repository;

import com.pk.core.model.entity.BadgeFlowEntity;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BadgeFlowRepo extends PkRepo<BadgeFlowEntity, Long> {

    /** Xoá luật của 1 mẫu nhãn khi mẫu đó bị xoá (khớp ON DELETE CASCADE ở DB cho trường hợp hard
     * delete thật, nhưng BadgeTemplateEntity xoá MỀM nên hàm này KHÔNG được gọi tự động - chỉ để sẵn
     * cho trường hợp cần dọn tay sau này). */
    List<BadgeFlowEntity> findByBadgeId(@Param("badgeId") Long badgeId);
}
