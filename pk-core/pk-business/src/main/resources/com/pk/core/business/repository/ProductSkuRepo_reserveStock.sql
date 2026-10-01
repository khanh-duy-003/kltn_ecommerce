UPDATE product_skus
   SET reserved = reserved + /*qty*/1,
       updated_date = CURRENT_TIMESTAMP
 WHERE id = /*skuId*/1
   AND on_hand - reserved >= /*qty*/1
