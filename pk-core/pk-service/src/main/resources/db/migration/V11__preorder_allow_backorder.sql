-- Pre-order: cho phép reserved vượt on_hand (backorder) với SKU đã hết hàng.
ALTER TABLE product_skus DROP CONSTRAINT IF EXISTS ck_product_skus_reserved_lte_on_hand;
