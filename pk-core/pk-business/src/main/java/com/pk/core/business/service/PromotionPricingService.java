package com.pk.core.business.service;

import java.math.BigDecimal;

/** Tính khuyến mãi sản phẩm (markdown/đồng giá) áp vào giá bán - dùng chung cho giỏ hàng, báo giá
 * checkout, tạo đơn và hiển thị giá storefront để mọi nơi ra cùng một con số. Quy ước: lấy khuyến mãi
 * đang hiệu lực có mức giảm LỚN NHẤT trên mỗi đơn vị, KHÔNG cộng dồn nhiều chương trình; giá nền
 * là giá thực bán của SKU (đã gồm sale_price nếu có). */
public interface PromotionPricingService {

    /** Số tiền giảm trên 1 đơn vị sản phẩm `productId` bán giá `unitPrice`; 0 nếu không có khuyến mãi. */
    BigDecimal unitDiscount(Long productId, BigDecimal unitPrice);
}
