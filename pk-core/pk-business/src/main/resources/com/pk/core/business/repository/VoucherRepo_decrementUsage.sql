UPDATE vouchers
   SET used_count = GREATEST(used_count - 1, 0),
       updated_date = CURRENT_TIMESTAMP
 WHERE id = /*id*/1
