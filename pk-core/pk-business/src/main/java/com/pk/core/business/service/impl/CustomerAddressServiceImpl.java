package com.pk.core.business.service.impl;

import lombok.RequiredArgsConstructor;

import com.pk.core.business.repository.CustomerAddressRepo;
import com.pk.core.business.service.CustomerAddressService;
import com.pk.core.common.exception.ResourceNotFoundException;
import com.pk.core.model.dto.request.AddressRequestDto;
import com.pk.core.model.dto.response.AddressResponseDto;
import com.pk.core.model.entity.CustomerAddressEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerAddressServiceImpl implements CustomerAddressService {

    private final CustomerAddressRepo addresses;

    @Transactional(readOnly = true)
    @Override
    public List<AddressResponseDto> listMine(Long userId) {
        return addresses.findByUserId(userId).stream().map(AddressResponseDto::from).toList();
    }

    @Transactional
    @Override
    public AddressResponseDto add(Long userId, AddressRequestDto req) {
        if (req.isDefault()) {
            addresses.clearDefaultForUser(userId);
        }
        CustomerAddressEntity entity = new CustomerAddressEntity(userId, req.getRecipientName(), req.getPhone(),
                req.getProvince(), req.getDistrict(), req.getWard(), req.getAddressLine());
        entity.setDefault(req.isDefault());
        addresses.create(entity);
        return AddressResponseDto.from(entity);
    }

    @Transactional
    @Override
    public AddressResponseDto update(Long userId, Long addressId, AddressRequestDto req) {
        CustomerAddressEntity entity = addresses.findByIdAndUserId(addressId, userId);
        if (entity == null) {
            throw new ResourceNotFoundException("Địa chỉ", "Address", addressId);
        }
        if (req.isDefault() && !entity.isDefault()) {
            addresses.clearDefaultForUser(userId);
        }
        entity.setRecipientName(req.getRecipientName());
        entity.setPhone(req.getPhone());
        entity.setProvince(req.getProvince());
        entity.setDistrict(req.getDistrict());
        entity.setWard(req.getWard());
        entity.setAddressLine(req.getAddressLine());
        entity.setDefault(req.isDefault());
        entity.touch();
        addresses.update(entity);
        return AddressResponseDto.from(entity);
    }

    @Transactional
    @Override
    public void delete(Long userId, Long addressId) {
        CustomerAddressEntity entity = addresses.findByIdAndUserId(addressId, userId);
        if (entity == null) {
            throw new ResourceNotFoundException("Địa chỉ", "Address", addressId);
        }
        // Xoá cứng (không soft-delete): customer_addresses không bị orders tham chiếu FK (địa chỉ được
        // snapshot sang cột ship_* của orders lúc đặt hàng), nên xoá thẳng không ảnh hưởng đơn cũ.
        addresses.delete(entity);
    }
}
