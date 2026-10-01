UPDATE product_skus
   SET on_hand = on_hand + /*delta*/0,
       updated_date = CURRENT_TIMESTAMP
 WHERE id = /*skuId*/1
   AND on_hand + /*delta*/0 >= 0
