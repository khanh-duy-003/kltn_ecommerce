package com.pk.core.business.service.impl;

import com.pk.core.business.repository.ProductMediaRepo;
import com.pk.core.business.repository.ProductRepo;
import com.pk.core.business.repository.ProductSkuRepo;
import com.pk.core.business.service.ProductMediaService;
import com.pk.core.common.exception.BusinessException;
import com.pk.core.common.exception.ErrorCode;
import com.pk.core.common.exception.ResourceNotFoundException;
import com.pk.core.model.dto.request.AdminProductMediaRequestDto;
import com.pk.core.model.dto.response.ProductMediaResponseDto;
import com.pk.core.model.entity.ProductEntity;
import com.pk.core.model.entity.ProductMediaEntity;
import com.pk.core.model.entity.ProductSkuEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ProductMediaServiceImpl implements ProductMediaService {

    private final ProductMediaRepo media;
    private final ProductRepo products;
    private final ProductSkuRepo productSkus;

    @Transactional(readOnly = true)
    @Override
    public List<ProductMediaResponseDto> findByProduct(Long productId) {
        requireProduct(productId);
        return media.findByProductId(productId).stream().map(ProductMediaResponseDto::from).toList();
    }

    @Transactional
    @Override
    public ProductMediaResponseDto add(Long productId, AdminProductMediaRequestDto req) {
        ProductEntity product = requireProduct(productId);
        ProductMediaEntity e = new ProductMediaEntity();
        e.setProductId(productId);
        apply(e, productId, req);
        media.create(e);
        syncPrimary(product, e.getId(), Boolean.TRUE.equals(req.getPrimary()));
        return ProductMediaResponseDto.from(media.findOne(e.getId()));
    }

    @Transactional
    @Override
    public ProductMediaResponseDto update(Long productId, Long mediaId, AdminProductMediaRequestDto req) {
        ProductEntity product = requireProduct(productId);
        ProductMediaEntity e = requireMedia(productId, mediaId);
        apply(e, productId, req);
        e.touch();
        media.update(e);
        syncPrimary(product, e.getId(), Boolean.TRUE.equals(req.getPrimary()));
        return ProductMediaResponseDto.from(media.findOne(mediaId));
    }

    @Transactional
    @Override
    public void delete(Long productId, Long mediaId) {
        ProductEntity product = requireProduct(productId);
        ProductMediaEntity e = requireMedia(productId, mediaId);
        media.delete(e);
        syncPrimary(product, null, false);
    }

    private void apply(ProductMediaEntity e, Long productId, AdminProductMediaRequestDto req) {
        String url = req.getUrl() == null ? "" : req.getUrl().trim();
        if (url.isEmpty()) {
            throw BusinessException.badRequest(ErrorCode.VALIDATION_FAILED, "url không được để trống");
        }
        String type = req.getType() == null || req.getType().isBlank()
                ? ProductMediaEntity.IMAGE : req.getType().trim().toUpperCase(Locale.ROOT);
        if (!ProductMediaEntity.IMAGE.equals(type) && !ProductMediaEntity.VIDEO.equals(type)) {
            throw BusinessException.badRequest(ErrorCode.VALIDATION_FAILED,
                    "type không hợp lệ: " + req.getType() + " (IMAGE | VIDEO)", req.getType());
        }
        if (req.getSkuId() != null) {
            ProductSkuEntity sku = productSkus.findOne(req.getSkuId());
            if (sku == null || !productId.equals(sku.getProductId())) {
                throw new ResourceNotFoundException("SKU", "ProductSku", req.getSkuId());
            }
        }
        e.setUrl(url);
        e.setAlt(req.getAlt());
        e.setMediaType(type);
        e.setSkuId(req.getSkuId());
        if (req.getSortOrder() != null) {
            e.setSortOrder(req.getSortOrder());
        }
    }

    /**
     * Giữ đúng 1 ảnh chính trong các ảnh chung (skuId null, IMAGE) của sản phẩm và đồng bộ products.thumbnail_url.
     * Ưu tiên: media vừa được yêu cầu làm chính > ảnh chính hiện có > ảnh chung đầu tiên. Hết ảnh chung thì không đụng thumbnail.
     */
    private void syncPrimary(ProductEntity product, Long preferId, boolean preferWins) {
        List<ProductMediaEntity> all = media.findByProductId(product.getId());
        List<ProductMediaEntity> shared = all.stream()
                .filter(m -> m.getSkuId() == null && ProductMediaEntity.IMAGE.equals(m.getMediaType()))
                .toList();
        // Mọi media không đủ điều kiện (riêng SKU / VIDEO) không được là ảnh chính.
        for (ProductMediaEntity m : all) {
            if (m.isPrimary() && !shared.contains(m)) {
                m.setPrimary(false);
                media.update(m);
            }
        }
        if (shared.isEmpty()) {
            return;
        }
        ProductMediaEntity chosen = null;
        if (preferWins && preferId != null) {
            chosen = shared.stream().filter(m -> preferId.equals(m.getId())).findFirst().orElse(null);
        }
        if (chosen == null) {
            chosen = shared.stream().filter(ProductMediaEntity::isPrimary).findFirst().orElse(shared.get(0));
        }
        for (ProductMediaEntity m : shared) {
            boolean primary = m.getId().equals(chosen.getId());
            if (m.isPrimary() != primary) {
                m.setPrimary(primary);
                media.update(m);
            }
        }
        if (!chosen.getUrl().equals(product.getThumbnailUrl())) {
            product.setThumbnailUrl(chosen.getUrl());
            product.touch();
            products.update(product);
        }
    }

    private ProductEntity requireProduct(Long productId) {
        ProductEntity p = productId == null ? null : products.findOne(productId);
        if (p == null || p.getDeletedDate() != null) {
            throw new ResourceNotFoundException("Sản phẩm", "Product", productId);
        }
        return p;
    }

    private ProductMediaEntity requireMedia(Long productId, Long mediaId) {
        ProductMediaEntity e = mediaId == null ? null : media.findOne(mediaId);
        if (e == null || !productId.equals(e.getProductId())) {
            throw new ResourceNotFoundException("Media", "ProductMedia", mediaId);
        }
        return e;
    }
}
