package com.pk.core.common.dto;

import com.pk.core.common.entity.UpdateEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/** DTO tương ứng UpdateEntity: thêm ai sửa, lúc nào. */
@Getter
@Setter
@NoArgsConstructor
public abstract class UpdateDto extends CreateDto {

    private Long updatedId;

    private Date updatedDate;

    public void copyAudit(UpdateEntity e) {
        super.copyAudit(e);
        this.updatedId = e.getUpdatedId();
        this.updatedDate = e.getUpdatedDate();
    }
}
