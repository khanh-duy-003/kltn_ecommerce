SELECT *
  FROM phone_otps
 WHERE phone = /*phone*/'0901234567'
   AND purpose = /*purpose*/'REGISTER'
 ORDER BY created_date DESC, id DESC
 LIMIT 1
