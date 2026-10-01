package com.pk.core.common.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Tầng 1 của chuỗi audit: ai tạo, lúc nào.
 * Không gắn annotation ORM để module độc lập: tên cột suy ra từ tên field
 * (createdId -> created_id, createdDate -> created_date) nhờ NameConverter cấu hình ở ứng dụng.
 * Mirage không có callback tự động: createdDate tự có giá trị khi new entity; createdId do service gán.
 */
@Getter
@Setter
public abstract class CreateEntity {

    private Long createdId;

    private Date createdDate = new Date();
}
