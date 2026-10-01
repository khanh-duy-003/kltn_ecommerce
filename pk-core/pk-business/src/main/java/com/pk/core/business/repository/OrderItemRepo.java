package com.pk.core.business.repository;

import com.pk.core.model.entity.OrderItemEntity;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderItemRepo extends PkRepo<OrderItemEntity, Long> {

    List<OrderItemEntity> findByOrderId(@Param("orderId") Long orderId);
}
