SELECT *
  FROM orders
 WHERE (/*status*/'' = '' OR status = /*status*/'')
   AND (/*paymentStatus*/'' = '' OR payment_status = /*paymentStatus*/'')
   AND (/*keyword*/'' = ''
        OR LOWER(code) LIKE LOWER('%'||/*keyword*/''||'%')
        OR LOWER(ship_recipient_name) LIKE LOWER('%'||/*keyword*/''||'%')
        OR ship_phone LIKE '%'||/*keyword*/''||'%')
 ORDER BY placed_at DESC
