package com.pk.core.business.service.impl;

import lombok.RequiredArgsConstructor;

import com.pk.core.business.repository.ProductRepo;
import com.pk.core.business.repository.WishlistItemRepo;
import com.pk.core.business.service.ProductService;
import com.pk.core.business.service.WishlistService;
import com.pk.core.common.exception.ResourceNotFoundException;
import com.pk.core.model.dto.response.WishlistIdsResponseDto;
import com.pk.core.model.dto.response.WishlistItemResponseDto;
import com.pk.core.model.dto.response.WishlistStatusResponseDto;
import com.pk.core.model.entity.ProductEntity;
import com.pk.core.model.entity.WishlistItemEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final WishlistItemRepo wishlist;
    private final ProductRepo products;
    private final ProductService productService;

    @Transactional(readOnly = true)
    @Override
    public List<WishlistItemResponseDto> list(Long userId) {
        List<WishlistItemResponseDto> result = new ArrayList<>();
        for (WishlistItemEntity item : wishlist.findByUserId(userId)) {
            ProductEntity p = products.findOne(item.getProductId());
            if (p == null || p.getDeletedDate() != null || !p.isPublished()) {
                continue;
            }
            result.add(new WishlistItemResponseDto(String.valueOf(p.getId()), p.getName(), p.getSlug(),
                    p.getThumbnailUrl(), p.getBasePrice() == null ? null : p.getBasePrice().longValue(),
                    item.getCreatedDate(), productService.findBySlug(p.getSlug())));
        }
        return result;
    }

    @Transactional(readOnly = true)
    @Override
    public WishlistIdsResponseDto ids(Long userId) {
        return idsOf(userId);
    }

    @Transactional
    @Override
    public WishlistIdsResponseDto add(Long userId, List<String> productIds) {
        // Kiểm tra hết sản phẩm TRƯỚC khi ghi để 404 không để lại phần thêm dở.
        Set<Long> ids = parseIds(productIds);
        for (Long id : ids) {
            ProductEntity p = products.findOne(id);
            if (p == null || p.getDeletedDate() != null || !p.isPublished()) {
                throw new ResourceNotFoundException("Sản phẩm", "Product", id);
            }
        }
        for (Long id : ids) {
            if (wishlist.findByUserIdAndProductId(userId, id) == null) {
                wishlist.create(new WishlistItemEntity(userId, id));
            }
        }
        return idsOf(userId);
    }

    @Transactional
    @Override
    public WishlistIdsResponseDto remove(Long userId, List<String> productIds) {
        for (Long id : parseIds(productIds)) {
            wishlist.deleteByUserIdAndProductId(userId, id);
        }
        return idsOf(userId);
    }

    @Transactional(readOnly = true)
    @Override
    public WishlistStatusResponseDto status(Long userId, String productId) {
        Long id = parseId(productId);
        boolean wishlisted = id != null && wishlist.findByUserIdAndProductId(userId, id) != null;
        return new WishlistStatusResponseDto(productId, wishlisted);
    }

    private WishlistIdsResponseDto idsOf(Long userId) {
        List<String> ids = new ArrayList<>();
        for (WishlistItemEntity item : wishlist.findByUserId(userId)) {
            ids.add(String.valueOf(item.getProductId()));
        }
        return new WishlistIdsResponseDto(ids);
    }

    private static Set<Long> parseIds(List<String> productIds) {
        Set<Long> ids = new LinkedHashSet<>();
        for (String s : productIds) {
            Long id = parseId(s);
            if (id != null) {
                ids.add(id);
            }
        }
        return ids;
    }

    /** DTO đã kiểm regex số, nhưng productId ở path không qua DTO nên vẫn phải chịu được chuỗi lạ (-> null). */
    private static Long parseId(String s) {
        if (s == null) {
            return null;
        }
        try {
            return Long.valueOf(s.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
