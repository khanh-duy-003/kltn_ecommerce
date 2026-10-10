INSERT INTO product_collections (product_id, collection_id)
SELECT /*productId*/1, col.id
  FROM collections col
 WHERE col.id = /*collectionId*/1
   AND NOT EXISTS (SELECT 1 FROM product_collections pc
                    WHERE pc.product_id = /*productId*/1 AND pc.collection_id = /*collectionId*/1)
