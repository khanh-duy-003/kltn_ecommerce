package com.pk.core.business.repository;

import com.pk.core.model.entity.OrderTimelineEntity;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderTimelineRepo extends PkRepo<OrderTimelineEntity, Long> {

    List<OrderTimelineEntity> findByOrderId(@Param("orderId") Long orderId);
}
