SELECT *
  FROM banners
 WHERE placement_code = /*placementCode*/'HOME_HERO'
   AND status = 'ACTIVE'
   AND deleted_date IS NULL
 ORDER BY sort_order, id
