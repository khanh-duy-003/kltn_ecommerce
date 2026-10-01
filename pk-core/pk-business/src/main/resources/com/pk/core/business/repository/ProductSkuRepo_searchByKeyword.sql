SELECT *
  FROM product_skus
 WHERE /*keyword*/'' = ''
    OR LOWER(sku_code) LIKE LOWER('%'||/*keyword*/''||'%')
    OR LOWER(name) LIKE LOWER('%'||/*keyword*/''||'%')
