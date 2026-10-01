SELECT *
  FROM order_timeline
 WHERE order_id = /*orderId*/1
 ORDER BY occurred_at ASC, id ASC
