package com.pk.core.business.service;

import com.pk.core.model.dto.request.AddressRequestDto;
import com.pk.core.model.dto.response.AddressResponseDto;

import java.util.List;

/** Sổ địa chỉ giao hàng của khách (GET/POST/PUT/DELETE /storefront/me/addresses) - CHƯA gồm
 * GET/PUT /storefront/me (sửa tên/email hồ sơ) vì đó là dữ liệu của UserEntity, thuộc phạm vi
 * identity/auth mà người dùng đã yêu cầu tạm gác lại ("ngoài admin và login"). */
public interface CustomerAddressService {

    List<AddressResponseDto> listMine(Long userId);

    AddressResponseDto add(Long userId, AddressRequestDto req);

    AddressResponseDto update(Long userId, Long addressId, AddressRequestDto req);

    void delete(Long userId, Long addressId);
}
