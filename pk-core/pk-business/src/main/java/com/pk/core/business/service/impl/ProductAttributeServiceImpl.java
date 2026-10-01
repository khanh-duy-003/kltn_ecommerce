package com.pk.core.business.service.impl;

import lombok.RequiredArgsConstructor;

import com.pk.core.business.repository.ProductAttributeRepo;
import com.pk.core.business.service.ProductAttributeService;
import com.pk.core.common.exception.BusinessException;
import com.pk.core.common.exception.ErrorCode;
import com.pk.core.common.exception.ResourceNotFoundException;
import com.pk.core.model.dto.request.AdminAttributeRequestDto;
import com.pk.core.model.dto.response.ProductAttributeResponseDto;
import com.pk.core.model.entity.ProductAttributeEntity;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductAttributeServiceImpl implements ProductAttributeService {

    private final ProductAttributeRepo attributes;

    @Transactional(readOnly = true)
    @Override
    public List<ProductAttributeResponseDto> findAll() {
        return attributes.findAll(Sort.unsorted()).stream().map(ProductAttributeResponseDto::from).toList();
    }

    @Transactional
    @Override
    public ProductAttributeResponseDto create(AdminAttributeRequestDto req) {
        String code = req.getCode().trim();
        if (attributes.findByCode(code) != null) {
            throw BusinessException.conflict(ErrorCode.DUPLICATE_CODE, "Code thuộc tính đã tồn tại: " + code, code);
        }
        ProductAttributeEntity e = new ProductAttributeEntity(req.getName().trim(), code,
                req.getType().trim().toUpperCase(), null);
        e.setOptionsList(req.getOptions());
        attributes.create(e);
        return ProductAttributeResponseDto.from(e);
    }

    @Transactional
    @Override
    public ProductAttributeResponseDto update(Long attributeId, AdminAttributeRequestDto req) {
        ProductAttributeEntity e = attributes.findOne(attributeId);
        if (e == null) {
            throw new ResourceNotFoundException("Thuộc tính", "Attribute", attributeId);
        }
        String code = req.getCode().trim();
        if (!code.equalsIgnoreCase(e.getCode()) && attributes.findByCode(code) != null) {
            throw BusinessException.conflict(ErrorCode.DUPLICATE_CODE, "Code thuộc tính đã tồn tại: " + code, code);
        }
        e.setName(req.getName().trim());
        e.setCode(code);
        e.setType(req.getType().trim().toUpperCase());
        e.setOptionsList(req.getOptions());
        e.touch();
        attributes.update(e);
        return ProductAttributeResponseDto.from(e);
    }
}
