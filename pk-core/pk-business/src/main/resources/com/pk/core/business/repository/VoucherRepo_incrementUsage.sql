UPDATE vouchers
   SET used_count = used_count + 1,
       updated_date = CURRENT_TIMESTAMP
 WHERE id = /*id*/1
   AND (usage_limit IS NULL OR used_count < usage_limit)
