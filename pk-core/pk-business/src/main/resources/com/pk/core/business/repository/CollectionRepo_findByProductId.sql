SELECT col.*
  FROM collections col
  JOIN product_collections pc ON pc.collection_id = col.id
 WHERE pc.product_id = /*productId*/1
 ORDER BY col.name ASC
