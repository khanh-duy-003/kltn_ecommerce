package com.pk.core.common.dto;

import com.pk.core.common.entity.DeleteEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/** DTO tương ứng DeleteEntity: thêm ai xoá, lúc nào. */
@Getter
@Setter
@NoArgsConstructor
public abstract class DeleteDto extends UpdateDto {

    private Long deletedId;

    private Date deletedDate;

    public void copyAudit(DeleteEntity e) {
        super.copyAudit(e);
        this.deletedId = e.getDeletedId();
        this.deletedDate = e.getDeletedDate();
    }
}
