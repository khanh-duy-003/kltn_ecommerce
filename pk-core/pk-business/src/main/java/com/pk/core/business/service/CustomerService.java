package com.pk.core.business.service;

import com.pk.core.common.web.PageResponse;
import com.pk.core.model.dto.response.AdminCustomerDetailResponseDto;
import com.pk.core.model.dto.response.AdminCustomerResponseDto;

/** Admin mục N (document/09-tong-hop-api-fe.md) - CHỈ đọc (spec không có API tạo/sửa/xoá khách hàng
 * từ phía admin, tài khoản khách tự đăng ký qua /storefront/auth). */
public interface CustomerService {

    PageResponse<AdminCustomerResponseDto> search(String keyword, int page, int take);

    AdminCustomerDetailResponseDto findByIdForAdmin(Long customerId);
}
