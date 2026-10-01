SELECT *
  FROM customer_addresses
 WHERE user_id = /*userId*/1
 ORDER BY is_default DESC, id DESC
