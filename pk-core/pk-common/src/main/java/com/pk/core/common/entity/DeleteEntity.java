package com.pk.core.common.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/** Tầng 3: ai xoá, lúc nào (xoá mềm; null nghĩa là chưa xoá). */
@Getter
@Setter
public abstract class DeleteEntity extends UpdateEntity {

    private Long deletedId;

    private Date deletedDate;
}
