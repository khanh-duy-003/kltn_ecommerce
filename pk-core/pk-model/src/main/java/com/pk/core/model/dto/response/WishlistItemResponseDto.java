package com.pk.core.model.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/** Một sản phẩm trong danh sách yêu thích (spec FE chưa định nghĩa schema response, đây là bản tối thiểu đủ để hiển thị). */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WishlistItemResponseDto {

    private String productId;
    private String name;
    private String slug;
    private String thumbnailUrl;
    /** Giá cơ sở của sản phẩm (products.base_price), số nguyên VND. */
    private Long basePrice;
    /** Thời điểm thêm vào yêu thích. */
    private Date addedAt;
    /** Sản phẩm đầy đủ (giá đã trừ khuyến mãi, cùng nguồn với trang sản phẩm) - không đưa trực tiếp ra JSON. */
    @JsonIgnore
    private ProductResponseDto product;

    // ---- Field theo ApiProduct của FE (danh sách yêu thích) - lấy từ `product` để giá khớp trang sản phẩm ----

    public String getProductName() {
        return product != null ? product.getProductName() : name;
    }

    public String getProductSlug() {
        return product != null ? product.getProductSlug() : slug;
    }

    public String getProductStatus() {
        return product != null ? product.getProductStatus() : null;
    }

    public boolean getIsPurchasable() {
        return product != null && product.getIsPurchasable();
    }

    public String getShortDescription() {
        return product != null ? product.getShortDescription() : null;
    }

    public String getImage() {
        return product != null ? product.getImage() : (thumbnailUrl == null ? "" : thumbnailUrl);
    }

    public String getImageHover() {
        return getImage();
    }

    public String getBrandName() {
        return product != null ? product.getBrandName() : null;
    }

    public java.util.Map<String, Object> getPricing() {
        return product != null ? product.getPricing() : null;
    }

    public java.util.Map<String, Object> getDefaultDisplay() {
        return product != null ? product.getDefaultDisplay() : null;
    }

    public boolean getRequiresSelectionDialog() {
        return product != null && product.getRequiresSelectionDialog();
    }

    public String getStockStatus() {
        return product != null ? product.getStockStatus() : null;
    }

    public String getDefaultVariationId() {
        return product != null ? product.getDefaultVariationId() : null;
    }

    public String getUpdatedAt() {
        return product != null ? product.getUpdatedAt() : null;
    }

    public java.util.Map<String, Object> getPricePresentation() {
        return product != null ? product.getPricePresentation() : null;
    }
}
