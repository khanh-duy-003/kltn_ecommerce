UPDATE refresh_tokens
   SET revoked_at = /*now*/CURRENT_TIMESTAMP,
       updated_date = /*now*/CURRENT_TIMESTAMP
 WHERE family_id = /*familyId*/'f'
   AND revoked_at IS NULL
