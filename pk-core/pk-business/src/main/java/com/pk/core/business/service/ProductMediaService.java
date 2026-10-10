package com.pk.core.business.service;

import com.pk.core.model.dto.request.AdminProductMediaRequestDto;
import com.pk.core.model.dto.response.ProductMediaResponseDto;

import java.util.List;

public interface ProductMediaService {

    List<ProductMediaResponseDto> findByProduct(Long productId);

    ProductMediaResponseDto add(Long productId, AdminProductMediaRequestDto req);

    ProductMediaResponseDto update(Long productId, Long mediaId, AdminProductMediaRequestDto req);

    void delete(Long productId, Long mediaId);
}
