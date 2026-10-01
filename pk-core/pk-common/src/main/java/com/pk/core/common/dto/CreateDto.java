package com.pk.core.common.dto;

import com.pk.core.common.entity.CreateEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/** DTO tương ứng CreateEntity: ai tạo, lúc nào. */
@Getter
@Setter
@NoArgsConstructor
public abstract class CreateDto {

    private Long createdId;

    private Date createdDate;

    public void copyAudit(CreateEntity e) {
        this.createdId = e.getCreatedId();
        this.createdDate = e.getCreatedDate();
    }
}
