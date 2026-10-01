SELECT *
  FROM orders
 WHERE user_id = /*userId*/1
 ORDER BY placed_at DESC, id DESC
