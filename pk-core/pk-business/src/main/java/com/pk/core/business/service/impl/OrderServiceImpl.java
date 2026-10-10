package com.pk.core.business.service.impl;

import lombok.RequiredArgsConstructor;

import com.pk.core.business.repository.CustomerAddressRepo;
import com.pk.core.business.repository.OrderItemRepo;
import com.pk.core.business.repository.OrderRepo;
import com.pk.core.business.repository.OrderTimelineRepo;
import com.pk.core.business.repository.PaymentRepo;
import com.pk.core.business.repository.ProductRepo;
import com.pk.core.business.repository.ProductSkuRepo;
import com.pk.core.business.service.OrderService;
import com.pk.core.business.service.PromotionPricingService;
import com.pk.core.business.service.PreOrderService;
import com.pk.core.business.service.VoucherService;
import com.pk.core.common.exception.BusinessException;
import com.pk.core.common.exception.ErrorCode;
import com.pk.core.common.exception.ResourceNotFoundException;
import com.pk.core.common.web.PageResponse;
import com.pk.core.model.dto.request.AdminOrderStatusRequestDto;
import com.pk.core.model.dto.request.CreateOrderRequestDto;
import com.pk.core.model.dto.request.OrderItemRequestDto;
import com.pk.core.model.dto.response.OrderItemResponseDto;
import com.pk.core.model.dto.response.OrderResponseDto;
import com.pk.core.model.dto.response.OrderTimelineResponseDto;
import com.pk.core.model.entity.CustomerAddressEntity;
import com.pk.core.model.entity.OrderEntity;
import com.pk.core.model.entity.OrderItemEntity;
import com.pk.core.model.entity.OrderTimelineEntity;
import com.pk.core.model.entity.PaymentEntity;
import com.pk.core.model.entity.ProductEntity;
import com.pk.core.model.entity.ProductSkuEntity;
import com.pk.core.model.entity.VoucherEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepo orders;
    private final OrderItemRepo orderItems;
    private final OrderTimelineRepo orderTimelines;
    private final PaymentRepo payments;
    private final CustomerAddressRepo addresses;
    private final ProductRepo products;
    private final ProductSkuRepo productSkus;
    private final VoucherService voucherService;
    private final PromotionPricingService promotionPricing;
    private final PreOrderService preOrder;

    @Transactional
    @Override
    public OrderResponseDto create(Long userId, CreateOrderRequestDto req) {
        CustomerAddressEntity address = addresses.findByIdAndUserId(req.getAddressId(), userId);
        if (address == null) {
            throw new ResourceNotFoundException("Địa chỉ giao hàng", "Address", req.getAddressId());
        }

        // Gộp trùng skuId (client lỡ gửi 2 dòng cùng 1 SKU) để chỉ giữ chỗ tồn kho + tạo 1 dòng duy nhất.
        Map<Long, Integer> qtyBySku = new LinkedHashMap<>();
        for (OrderItemRequestDto line : req.getItems()) {
            qtyBySku.merge(line.getSkuId(), line.getQuantity(), Integer::sum);
        }

        List<OrderItemEntity> itemEntities = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal productDiscount = BigDecimal.ZERO;
        boolean hasPreOrderLine = false;
        // Mỗi lần reserveStock/tạo dòng hàng đều nằm trong transaction này: nếu 1 SKU giữa chừng
        // không đủ hàng và ném exception, Spring sẽ rollback TOÀN BỘ (kể cả các reserveStock đã chạy
        // trước đó trong vòng lặp) - không cần tự tay nhả lại tồn kho ở đây.
        for (Map.Entry<Long, Integer> line : qtyBySku.entrySet()) {
            Long skuId = line.getKey();
            int qty = line.getValue();

            ProductSkuEntity sku = productSkus.findOne(skuId);
            if (sku == null || !ProductSkuEntity.PUBLISHED.equals(sku.getStatus())) {
                throw new ResourceNotFoundException("Sản phẩm", "ProductSku", skuId);
            }
            // Đặt trước đang bật và SKU đang hết hàng: nhận đơn đặt trước (reserved vượt on_hand, available âm).
            boolean preOrderLine = sku.available() <= 0 && preOrder.isEnabled();
            int reserved = preOrderLine ? productSkus.reservePreOrderStock(skuId, qty)
                    : productSkus.reserveStock(skuId, qty);
            if (preOrderLine && reserved > 0) {
                hasPreOrderLine = true;
            }
            if (reserved == 0) {
                // args khớp placeholder {0} của messages_vi/en (INSUFFICIENT_STOCK) - available() đọc
                // TRƯỚC lần reserveStock vừa thất bại nên chỉ mang tính tham khảo (có thể vừa đổi do
                // request khác), đủ tốt cho thông báo UX, không cần chính xác tuyệt đối tức thời.
                throw BusinessException.badRequest(ErrorCode.INSUFFICIENT_STOCK,
                        "Sản phẩm \"" + sku.getName() + "\" không đủ hàng, chỉ còn " + sku.available(),
                        sku.available());
            }

            ProductEntity product = products.findOne(sku.getProductId());
            BigDecimal unitPrice = sku.effectivePrice();
            itemEntities.add(new OrderItemEntity(null, sku.getProductId(), skuId,
                    product != null ? product.getName() : sku.getName(),
                    product != null ? product.getThumbnailUrl() : null, qty, unitPrice));
            subtotal = subtotal.add(unitPrice.multiply(BigDecimal.valueOf(qty)));
            productDiscount = productDiscount.add(
                    promotionPricing.unitDiscount(sku.getProductId(), unitPrice).multiply(BigDecimal.valueOf(qty)));
        }

        VoucherEntity voucher = null;
        BigDecimal voucherDiscount = BigDecimal.ZERO;
        String appliedVoucherCode = null;
        if (req.getVoucherCode() != null && !req.getVoucherCode().isBlank()) {
            BigDecimal payable = subtotal.subtract(productDiscount);
            voucher = voucherService.validate(req.getVoucherCode(), payable);
            voucherDiscount = voucher.computeDiscount(payable);
            appliedVoucherCode = voucher.getCode();
        }

        // Khuyến mãi sản phẩm đã áp (PromotionPricingService); CHƯA tính phí ship (chưa có bảng phí theo
        // phương thức/khu vực) nên để 0.
        OrderEntity order = new OrderEntity(userId, req.getPaymentMethod(), req.getShippingMethod(), address,
                subtotal, productDiscount, voucherDiscount, BigDecimal.ZERO, appliedVoucherCode, req.getNote());
        orders.create(order);

        for (OrderItemEntity item : itemEntities) {
            item.setOrderId(order.getId());
            orderItems.create(item);
        }

        orderTimelines.create(new OrderTimelineEntity(order.getId(), OrderEntity.PENDING,
                hasPreOrderLine ? "Đơn hàng được tạo (có sản phẩm đặt trước)" : "Đơn hàng được tạo", "CUSTOMER"));

        if (voucher != null) {
            voucherService.applyUsage(voucher.getId(), voucher.getCode());
        }

        // Thanh toán online (khác COD): tạo bản ghi Payment PENDING, cổng thanh toán sẽ gọi lại
        // POST /storefront/payment/{paymentId}/callback (paymentId = id vừa tạo) - xem PaymentService.
        // COD thì không tạo Payment (paymentStatus của đơn giữ nguyên UNPAID, thu tiền khi giao).
        if (!"COD".equalsIgnoreCase(order.getPaymentMethod())) {
            payments.create(new PaymentEntity(order.getId(), order.getPaymentMethod(), order.getGrandTotal()));
        }

        return toDto(order);
    }

    @Transactional(readOnly = true)
    @Override
    public PageResponse<OrderResponseDto> listMine(Long userId, int page, int take) {
        List<OrderResponseDto> all = orders.findByUserId(userId).stream().map(this::toDto).toList();
        return PageResponse.paginate(all, page, take);
    }

    @Transactional(readOnly = true)
    @Override
    public OrderResponseDto findByCodeForUser(Long userId, String code) {
        OrderEntity order = orders.findByCodeAndUserId(code, userId);
        if (order == null) {
            throw new ResourceNotFoundException("Đơn hàng", "Order", code);
        }
        return toDto(order);
    }

    @Transactional
    @Override
    public OrderResponseDto cancel(Long userId, String code, String reason) {
        OrderEntity order = orders.findByCodeAndUserId(code, userId);
        if (order == null) {
            throw new ResourceNotFoundException("Đơn hàng", "Order", code);
        }
        // args khớp placeholder {0}/{1} của messages_vi/en (ORDER_ALREADY_CANCELLED/ORDER_STATUS_TRANSITION_INVALID).
        if (OrderEntity.CANCELLED.equals(order.getStatus())) {
            throw BusinessException.badRequest(ErrorCode.ORDER_ALREADY_CANCELLED,
                    "Đơn hàng " + order.getCode() + " đã được huỷ trước đó", order.getCode());
        }
        if (!order.isCancellableByCustomer()) {
            throw BusinessException.badRequest(ErrorCode.ORDER_STATUS_TRANSITION_INVALID,
                    "Đơn hàng đang ở trạng thái " + order.getStatus() + ", không thể tự huỷ",
                    order.getStatus(), OrderEntity.CANCELLED);
        }

        order.cancel();
        order.touch();
        orders.update(order);

        for (OrderItemEntity item : orderItems.findByOrderId(order.getId())) {
            productSkus.releaseStock(item.getSkuId(), item.getQuantity());
        }

        orderTimelines.create(new OrderTimelineEntity(order.getId(), OrderEntity.CANCELLED, reason, "CUSTOMER"));

        return toDto(order);
    }

    private OrderResponseDto toDto(OrderEntity o) {
        List<OrderItemResponseDto> items = orderItems.findByOrderId(o.getId()).stream()
                .map(OrderItemResponseDto::from).toList();
        List<OrderTimelineResponseDto> timeline = orderTimelines.findByOrderId(o.getId()).stream()
                .map(OrderTimelineResponseDto::from).toList();
        PaymentEntity payment = payments.findByOrderId(o.getId());
        return OrderResponseDto.from(o, payment != null ? payment.getId() : null, items, timeline);
    }

    // ============================== Admin (mục L spec) ==============================

    @Transactional(readOnly = true)
    @Override
    public PageResponse<OrderResponseDto> searchForAdmin(String search, String status, String paymentStatus,
                                                           String fromDate, String toDate, int page, int take) {
        Date from = parseAdminDate(fromDate, false);
        Date to = parseAdminDate(toDate, true);
        List<OrderResponseDto> all = orders.searchAdmin(status, paymentStatus, search).stream()
                .filter(o -> from == null || !o.getPlacedAt().before(from))
                .filter(o -> to == null || !o.getPlacedAt().after(to))
                .map(this::toDto)
                .toList();
        return PageResponse.paginate(all, page, take);
    }

    /** Parse "yyyy-MM-dd" thành mốc đầu ngày (endOfDay=false, dùng cho fromDate) hoặc cuối ngày
     * (endOfDay=true, dùng cho toDate) theo múi giờ hệ thống - null/rỗng trả null (không lọc). Sai
     * định dạng -> 400 VALIDATION_FAILED thay vì 500 nội bộ. */
    private Date parseAdminDate(String value, boolean endOfDay) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            LocalDate d = LocalDate.parse(value);
            LocalDateTime dt = endOfDay ? d.atTime(23, 59, 59) : d.atStartOfDay();
            return Date.from(dt.atZone(ZoneId.systemDefault()).toInstant());
        } catch (Exception ex) {
            throw BusinessException.badRequest(ErrorCode.VALIDATION_FAILED,
                    "Định dạng ngày không hợp lệ (yêu cầu yyyy-MM-dd): " + value, value);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public OrderResponseDto findByCodeForAdmin(String code) {
        OrderEntity order = orders.findByCode(code);
        if (order == null) {
            throw new ResourceNotFoundException("Đơn hàng", "Order", code);
        }
        return toDto(order);
    }

    @Transactional
    @Override
    public OrderResponseDto updateStatus(String code, AdminOrderStatusRequestDto request) {
        OrderEntity order = orders.findByCode(code);
        if (order == null) {
            throw new ResourceNotFoundException("Đơn hàng", "Order", code);
        }
        String oldStatus = order.getStatus();
        String newStatus = request.getStatus();
        // args khớp placeholder {0}/{1} của messages_vi/en (INVALID_STATUS_TRANSITION).
        if (!order.canTransitionTo(newStatus)) {
            throw BusinessException.conflict(ErrorCode.INVALID_STATUS_TRANSITION,
                    "Không thể chuyển đơn hàng từ trạng thái " + oldStatus + " sang " + newStatus,
                    oldStatus, newStatus);
        }

        order.setStatus(newStatus);
        if (request.getTrackingCode() != null && !request.getTrackingCode().isBlank()) {
            order.setTrackingCode(request.getTrackingCode());
        }
        order.touch();
        orders.update(order);

        // Chuyển sang CANCELLED qua updateStatus cũng phải nhả tồn như cancel()/cancelByAdmin().
        // CANCELLED không có trạng thái đi tiếp nên không thể nhả hai lần.
        if (OrderEntity.CANCELLED.equals(newStatus) && !OrderEntity.CANCELLED.equals(oldStatus)) {
            for (OrderItemEntity item : orderItems.findByOrderId(order.getId())) {
                productSkus.releaseStock(item.getSkuId(), item.getQuantity());
            }
        }

        String note = request.getNote() != null && !request.getNote().isBlank()
                ? request.getNote() : "Admin cập nhật trạng thái từ " + oldStatus + " sang " + newStatus;
        orderTimelines.create(new OrderTimelineEntity(order.getId(), newStatus, note, "ADMIN"));

        return toDto(order);
    }

    @Transactional
    @Override
    public OrderResponseDto cancelByAdmin(String code, String reason) {
        OrderEntity order = orders.findByCode(code);
        if (order == null) {
            throw new ResourceNotFoundException("Đơn hàng", "Order", code);
        }
        // Cùng quy ước cancel() của khách (xem javadoc phía trên) - chỉ khác isCancellableByAdmin().
        if (OrderEntity.CANCELLED.equals(order.getStatus())) {
            throw BusinessException.badRequest(ErrorCode.ORDER_ALREADY_CANCELLED,
                    "Đơn hàng " + order.getCode() + " đã được huỷ trước đó", order.getCode());
        }
        if (!order.isCancellableByAdmin()) {
            throw BusinessException.badRequest(ErrorCode.ORDER_STATUS_TRANSITION_INVALID,
                    "Đơn hàng đang ở trạng thái " + order.getStatus() + ", không thể huỷ",
                    order.getStatus(), OrderEntity.CANCELLED);
        }

        order.cancel();
        order.touch();
        orders.update(order);

        for (OrderItemEntity item : orderItems.findByOrderId(order.getId())) {
            productSkus.releaseStock(item.getSkuId(), item.getQuantity());
        }

        orderTimelines.create(new OrderTimelineEntity(order.getId(), OrderEntity.CANCELLED, reason, "ADMIN"));

        return toDto(order);
    }

    @Transactional
    @Override
    public OrderResponseDto returnOrder(String code, List<Long> lineIds) {
        OrderEntity order = orders.findByCode(code);
        if (order == null) {
            throw new ResourceNotFoundException("Đơn hàng", "Order", code);
        }
        if (!order.isReturnable()) {
            throw BusinessException.conflict(ErrorCode.INVALID_STATUS_TRANSITION,
                    "Chỉ xác nhận hoàn trả được khi đơn đang DELIVERED (hiện tại: " + order.getStatus() + ")",
                    order.getStatus(), OrderEntity.RETURNED);
        }

        List<OrderItemEntity> returnedItems = orderItems.findByOrderId(order.getId()).stream()
                .filter(item -> lineIds.contains(item.getId()))
                .toList();
        if (returnedItems.isEmpty()) {
            throw BusinessException.badRequest(ErrorCode.NOT_FOUND,
                    "Không tìm thấy dòng hàng khớp lineIds trong đơn " + code);
        }
        // Nhả lại phần tồn kho đã giữ chỗ (reserved) của CÁC DÒNG được hoàn trả - xem
        // ProductSkuRepo.releaseStock javadoc (mô hình hiện tại chưa có bước "chốt" on_hand lúc giao
        // hàng nên reserved vẫn còn giữ tới lúc này, giống hệt lúc huỷ đơn).
        for (OrderItemEntity item : returnedItems) {
            productSkus.releaseStock(item.getSkuId(), item.getQuantity());
        }

        order.markReturned();
        order.touch();
        orders.update(order);

        orderTimelines.create(new OrderTimelineEntity(order.getId(), OrderEntity.RETURNED,
                "Hoàn trả " + returnedItems.size() + " dòng hàng (lineIds=" + lineIds + ")", "ADMIN"));

        return toDto(order);
    }

    @Transactional
    @Override
    public OrderResponseDto refund(String code, BigDecimal amount, String reason) {
        OrderEntity order = orders.findByCode(code);
        if (order == null) {
            throw new ResourceNotFoundException("Đơn hàng", "Order", code);
        }
        if (!OrderEntity.PAID.equals(order.getPaymentStatus())) {
            throw BusinessException.conflict(ErrorCode.INVALID_STATUS_TRANSITION,
                    "Chỉ hoàn tiền được đơn đã thanh toán (paymentStatus hiện tại: "
                            + order.getPaymentStatus() + ")",
                    order.getPaymentStatus(), OrderEntity.REFUNDED);
        }

        order.setPaymentStatus(OrderEntity.REFUNDED);
        order.touch();
        orders.update(order);

        orderTimelines.create(new OrderTimelineEntity(order.getId(), order.getStatus(),
                "Hoàn tiền " + amount + " - Lý do: " + reason, "ADMIN"));

        return toDto(order);
    }
}
