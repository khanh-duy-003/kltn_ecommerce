package com.pk.core.model.dto.response;

import com.pk.core.common.dto.UpdateDto;
import com.pk.core.model.entity.OrderEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/** extends UpdateDto (không phải BaseDto): OrderEntity chỉ ở mức UpdateEntity (bảng `orders` không
 * có deleted_id/deleted_date - không xoá mềm đơn hàng), nên gọi thẳng copyAudit(OrderEntity) kế thừa
 * từ UpdateDto thay vì BaseDto.of(...) (BaseDto.of yêu cầu tham số kiểu BaseEntity, OrderEntity không
 * phải BaseEntity). */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDto extends UpdateDto {

    private Long id;
    private String code;
    private String status;
    private String paymentStatus;
    private String paymentMethod;
    private String shippingMethod;
    private String trackingCode;
    private String shipRecipientName;
    private String shipPhone;
    private String shipProvince;
    private String shipDistrict;
    private String shipWard;
    private String shipAddressLine;
    private BigDecimal subtotal;
    private BigDecimal productDiscount;
    private BigDecimal voucherDiscount;
    private BigDecimal shippingFee;
    private BigDecimal grandTotal;
    private String appliedVoucherCode;
    private String note;
    private Date placedAt;
    /** null nếu thanh toán COD (không tạo PaymentEntity) - xem OrderServiceImpl.create(). Dùng
     * paymentId này để gọi POST /storefront/payment/{paymentId}/callback. */
    private Long paymentId;
    private List<OrderItemResponseDto> items;
    private List<OrderTimelineResponseDto> timeline;

    public static OrderResponseDto from(OrderEntity o, Long paymentId, List<OrderItemResponseDto> items,
                                         List<OrderTimelineResponseDto> timeline) {
        OrderResponseDto dto = new OrderResponseDto(o.getId(), o.getCode(), o.getStatus(), o.getPaymentStatus(),
                o.getPaymentMethod(), o.getShippingMethod(), o.getTrackingCode(), o.getShipRecipientName(),
                o.getShipPhone(), o.getShipProvince(), o.getShipDistrict(), o.getShipWard(),
                o.getShipAddressLine(), o.getSubtotal(), o.getProductDiscount(), o.getVoucherDiscount(),
                o.getShippingFee(), o.getGrandTotal(), o.getAppliedVoucherCode(), o.getNote(), o.getPlacedAt(),
                paymentId, items, timeline);
        dto.copyAudit(o);
        return dto;
    }
}
