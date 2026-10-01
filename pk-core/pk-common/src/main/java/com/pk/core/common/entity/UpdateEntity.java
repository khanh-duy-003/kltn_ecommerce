package com.pk.core.common.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/** Tầng 2: ai sửa, lúc nào (null cho tới lần sửa đầu tiên). */
@Getter
@Setter
public abstract class UpdateEntity extends CreateEntity {

    private Long updatedId;

    private Date updatedDate;

    /** Gọi trước mỗi lần update để cập nhật updated_date. */
    public void touch() {
        this.updatedDate = new Date();
    }
}
