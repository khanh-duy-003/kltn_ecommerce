SELECT r.*
  FROM roles r
  JOIN user_roles ur ON ur.role_id = r.id
 WHERE ur.user_id = /*userId*/1
