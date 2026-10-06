UPDATE refresh_tokens
   SET revoked_at = /*now*/CURRENT_TIMESTAMP,
       updated_date = /*now*/CURRENT_TIMESTAMP
 WHERE user_id = /*userId*/1
   AND revoked_at IS NULL
