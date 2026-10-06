SELECT *
  FROM customer_requests
 WHERE type = /*type*/'BACK_IN_STOCK'
   AND contact_value = /*contactValue*/'0900000000'
   AND COALESCE(sku_id, 0) = /*skuId*/0
   AND COALESCE(order_code, '') = /*orderCode*/''
   AND status <> 'COMPLETED'
 ORDER BY id
 LIMIT 1
