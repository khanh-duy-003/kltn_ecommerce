SELECT DISTINCT u.*
  FROM users u
  JOIN user_roles ur ON ur.user_id = u.id
  JOIN roles r ON r.id = ur.role_id
 WHERE r.name = 'CUSTOMER'
   AND (/*keyword*/'' = ''
        OR LOWER(u.email) LIKE LOWER('%'||/*keyword*/''||'%')
        OR LOWER(u.full_name) LIKE LOWER('%'||/*keyword*/''||'%')
        OR u.phone LIKE '%'||/*keyword*/''||'%')
 ORDER BY u.created_date DESC
