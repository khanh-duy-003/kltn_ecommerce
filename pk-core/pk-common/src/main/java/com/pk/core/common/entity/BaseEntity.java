package com.pk.core.common.entity;

/**
 * Lớp cha cho mọi entity. Chuỗi kế thừa audit:
 * BaseEntity -> DeleteEntity(deletedId, deletedDate) -> UpdateEntity(updatedId, updatedDate)
 * -> CreateEntity(createdId, createdDate).
 */
public abstract class BaseEntity extends DeleteEntity {

}
