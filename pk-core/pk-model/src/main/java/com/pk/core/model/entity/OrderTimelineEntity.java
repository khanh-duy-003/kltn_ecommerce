package com.pk.core.model.entity;

import com.pk.core.model.constant.TableConstant;
import com.pk.core.common.entity.CreateEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import vn.com.unit.miragesql.miragesql.annotation.Column;
import vn.com.unit.miragesql.miragesql.annotation.PrimaryKey;
import vn.com.unit.miragesql.miragesql.annotation.PrimaryKey.GenerationType;
import vn.com.unit.miragesql.miragesql.annotation.Table;

import java.util.Date;

/** Lịch sử trạng thái đơn (hiển thị timeline cho khách). Bảng `order_timeline` chỉ có created_id/
 * created_date nên extends CreateEntity. */
@Getter
@Setter
@NoArgsConstructor
@Table(name = TableConstant.ORDER_TIMELINE)
public class OrderTimelineEntity extends CreateEntity {

    @Id
    @PrimaryKey(generationType = GenerationType.SEQUENCE, generator = "order_timeline_id_seq")
    @Column(name = "id")
    private Long id;

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "status")
    private String status;

    @Column(name = "occurred_at")
    private Date occurredAt = new Date();

    @Column(name = "note")
    private String note;

    @Column(name = "actor")
    private String actor;

    public OrderTimelineEntity(Long orderId, String status, String note, String actor) {
        this.orderId = orderId;
        this.status = status;
        this.note = note;
        this.actor = actor;
    }
}
