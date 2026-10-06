SELECT p.*
  FROM promotions p
  JOIN promotion_products pp ON pp.promotion_id = p.id
 WHERE pp.product_id = /*productId*/1
   AND p.status = 'PUBLISHED'
   AND p.deleted_date IS NULL
   AND p.starts_at <= /*now*/CURRENT_TIMESTAMP
   AND p.ends_at >= /*now*/CURRENT_TIMESTAMP
