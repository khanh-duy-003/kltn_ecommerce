SELECT *
  FROM wishlist_items
 WHERE user_id = /*userId*/1
 ORDER BY created_date DESC, id DESC
