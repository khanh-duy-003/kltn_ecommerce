package com.pk.core.business.service;

import com.pk.core.model.dto.request.BackInStockRequestDto;
import com.pk.core.model.dto.request.CustomerRequestBulkStatusRequestDto;
import com.pk.core.model.dto.request.NewsletterRequestDto;
import com.pk.core.model.dto.request.OrderSupportRequestDto;
import com.pk.core.model.dto.response.CustomerRequestPageResponseDto;
import com.pk.core.model.dto.response.CustomerRequestResponseDto;
import com.pk.core.model.dto.response.MessageResponseDto;

/** Yêu cầu khách hàng theo spec FE (nhóm Customer Request): 3 loại BACK_IN_STOCK / ORDER_SUPPORT / NEWSLETTER. Thêm 2026-10-06. */
public interface CustomerRequestService {

    /**
     * POST /storefront/customer-request/back-in-stock (công khai). SKU theo skuCode phải tồn tại (404). Gửi trùng
     * (cùng SĐT + SKU, yêu cầu cũ chưa COMPLETED) thì KHÔNG tạo thêm, vẫn trả thành công (idempotent).
     */
    MessageResponseDto createBackInStock(BackInStockRequestDto req);

    /**
     * POST /storefront/customer-request/order-support (công khai). Luôn ghi nhận kể cả mã đơn không khớp đơn nào (chỉ
     * gắn orderId khi tìm thấy) - cố ý KHÔNG trả 404 để người lạ không dò được mã đơn nào tồn tại.
     */
    MessageResponseDto createOrderSupport(OrderSupportRequestDto req);

    /** POST /storefront/customer-request/newsletter (công khai). Trùng email (chưa COMPLETED) thì không tạo thêm. */
    MessageResponseDto subscribeNewsletter(NewsletterRequestDto req);

    /**
     * GET /admin/customer-request/{back-in-stock|order-support|newsletter} - danh sách theo loại, mới nhất trước. Lọc
     * (rỗng/null = không lọc): keyword (chứa trong liên hệ/skuCode/tên sản phẩm/mã đơn), status, orderType, thời gian
     * theo timeFilter (Today, Yesterday, Last7Days, Last30Days, Last90Days, ThisMonth, LastMonth, Custom + timeFrom/timeTo
     * ISO-8601). Giá trị lạ -> 400. Lọc/cắt trang bằng Java; page 1-based, take mặc định 20 tối đa 100.
     */
    CustomerRequestPageResponseDto list(String type, int page, int take, String keyword, String status,
                                        String timeFilter, String timeFrom, String timeTo, String orderType);

    /** GET /admin/customer-request/{id} - 404 nếu không có. */
    CustomerRequestResponseDto findById(Long id);

    /** PATCH /admin/customer-request/{id}/status. */
    MessageResponseDto updateStatus(Long id, String status);

    /** PATCH /admin/customer-request/status/bulk - chỉ đổi những id tồn tại VÀ đúng type; id lạ/sai loại bị bỏ qua. */
    MessageResponseDto bulkUpdateStatus(CustomerRequestBulkStatusRequestDto req);
}
