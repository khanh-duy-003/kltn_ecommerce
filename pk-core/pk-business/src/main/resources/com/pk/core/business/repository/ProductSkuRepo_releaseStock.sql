UPDATE product_skus
   SET reserved = GREATEST(reserved - /*qty*/1, 0),
       updated_date = CURRENT_TIMESTAMP
 WHERE id = /*skuId*/1
