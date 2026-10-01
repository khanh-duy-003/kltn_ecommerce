UPDATE customer_addresses
   SET is_default = FALSE,
       updated_date = CURRENT_TIMESTAMP
 WHERE user_id = /*userId*/1
   AND is_default = TRUE
