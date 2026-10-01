package com.pk.core.business.service.impl;

import lombok.RequiredArgsConstructor;

import com.pk.core.business.repository.CustomerAddressRepo;
import com.pk.core.business.repository.ProductRepo;
import com.pk.core.business.repository.ProductSkuRepo;
import com.pk.core.business.service.CheckoutService;
import com.pk.core.business.service.VoucherService;
import com.pk.core.common.exception.BusinessException;
import com.pk.core.common.exception.ErrorCode;
import com.pk.core.common.exception.ResourceNotFoundException;
import com.pk.core.model.dto.request.OrderItemRequestDto;
import com.pk.core.model.dto.request.QuoteRequestDto;
import com.pk.core.model.dto.response.OrderItemResponseDto;
import com.pk.core.model.dto.response.QuoteResponseDto;
import com.pk.core.model.dto.response.QuoteSummaryResponseDto;
import com.pk.core.model.entity.CustomerAddressEntity;
import com.pk.core.model.entity.ProductEntity;
import com.pk.core.model.entity.ProductSkuEntity;
import com.pk.core.model.entity.VoucherEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CheckoutServiceImpl implements CheckoutService {

    private final CustomerAddressRepo addresses;
    private final ProductRepo products;
    private final ProductSkuRepo productSkus;
    private final VoucherService voucherService;

    @Transactional(readOnly = true)
    @Override
    public QuoteResponseDto quote(Long userId, QuoteRequestDto req) {
        CustomerAddressEntity address = addresses.findByIdAndUserId(req.getAddressId(), userId);
        if (address == null) {
            throw new ResourceNotFoundException("Địa chỉ giao hàng", "Address", req.getAddressId());
        }

        Map<Long, Integer> qtyBySku = new LinkedHashMap<>();
        for (OrderItemRequestDto line : req.getItems()) {
            qtyBySku.merge(line.getSkuId(), line.getQuantity(), Integer::sum);
        }

        List<OrderItemResponseDto> items = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        int itemCount = 0;
        for (Map.Entry<Long, Integer> line : qtyBySku.entrySet()) {
            Long skuId = line.getKey();
            int qty = line.getValue();

            ProductSkuEntity sku = productSkus.findOne(skuId);
            if (sku == null || !ProductSkuEntity.PUBLISHED.equals(sku.getStatus())) {
                throw new ResourceNotFoundException("Sản phẩm", "ProductSku", skuId);
            }
            if (sku.available() < qty) {
                throw BusinessException.badRequest(ErrorCode.INSUFFICIENT_STOCK,
                        "Sản phẩm \"" + sku.getName() + "\" không đủ hàng, chỉ còn " + sku.available(),
                        sku.available());
            }

            ProductEntity product = products.findOne(sku.getProductId());
            BigDecimal unitPrice = sku.effectivePrice();
            items.add(new OrderItemResponseDto(null, sku.getProductId(), skuId,
                    product != null ? product.getName() : sku.getName(),
                    product != null ? product.getThumbnailUrl() : null, qty, unitPrice,
                    unitPrice.multiply(BigDecimal.valueOf(qty))));
            subtotal = subtotal.add(unitPrice.multiply(BigDecimal.valueOf(qty)));
            itemCount += qty;
        }

        BigDecimal voucherDiscount = BigDecimal.ZERO;
        if (req.getVoucherCode() != null && !req.getVoucherCode().isBlank()) {
            VoucherEntity voucher = voucherService.validate(req.getVoucherCode(), subtotal);
            voucherDiscount = voucher.computeDiscount(subtotal);
        }

        // CHƯA có bảng phí ship theo phương thức/khu vực và CHƯA có domain Promotion - giống
        // OrderServiceImpl.create, để 0 (xem javadoc CheckoutService/OrderService).
        BigDecimal shippingFee = BigDecimal.ZERO;
        BigDecimal productDiscount = BigDecimal.ZERO;
        BigDecimal grandTotal = subtotal.subtract(productDiscount).subtract(voucherDiscount).add(shippingFee);

        QuoteSummaryResponseDto summary = new QuoteSummaryResponseDto(subtotal, productDiscount, voucherDiscount,
                shippingFee, grandTotal, itemCount);

        return new QuoteResponseDto(items, summary, estimatedDeliveryDate(req.getShippingMethod()));
    }

    /** CHƯA có logistics thật (thời gian giao theo khu vực/đơn vị vận chuyển) - ước lượng đơn giản:
     * EXPRESS +2 ngày, còn lại +5 ngày kể từ hôm nay. Chỉ mang tính tham khảo cho FE hiển thị. */
    private static Date estimatedDeliveryDate(String shippingMethod) {
        Calendar cal = Calendar.getInstance();
        int days = "EXPRESS".equalsIgnoreCase(shippingMethod) ? 2 : 5;
        cal.add(Calendar.DAY_OF_MONTH, days);
        return cal.getTime();
    }
}
