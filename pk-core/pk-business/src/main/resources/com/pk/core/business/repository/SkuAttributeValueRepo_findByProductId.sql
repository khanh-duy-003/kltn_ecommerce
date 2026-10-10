SELECT v.* FROM sku_attribute_values v
  JOIN product_skus s ON s.id = v.sku_id
 WHERE s.product_id = /*productId*/1
 ORDER BY v.id
