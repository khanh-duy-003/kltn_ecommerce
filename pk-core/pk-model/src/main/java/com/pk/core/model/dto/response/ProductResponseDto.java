package com.pk.core.model.dto.response;

import com.pk.core.common.dto.BaseDto;
import com.pk.core.model.entity.ProductEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDto extends BaseDto {

    private Long id;
    private String code;
    private String name;
    private String slug;
    private String shortDescription;
    private String description;
    private String status;
    private Long categoryId;
    private String categoryName;
    private String material;
    private String occasion;
    private String thumbnailUrl;
    /** Giá thấp nhất trong các SKU còn bán (PUBLISHED) - tính lại từ skus, không lấy thẳng cột basePrice. */
    private BigDecimal priceFrom;
    private List<ProductSkuResponseDto> skus;
    private List<CollectionResponseDto> collections;
    private Date publishedAt;

    public static ProductResponseDto from(ProductEntity p, String categoryName, BigDecimal priceFrom,
                                           List<ProductSkuResponseDto> skus, List<CollectionResponseDto> collections) {
        return BaseDto.of(new ProductResponseDto(p.getId(), p.getCode(), p.getName(), p.getSlug(),
                p.getShortDescription(), p.getDescription(), p.getStatus(), p.getCategoryId(), categoryName,
                p.getMaterial(), p.getOccasion(), p.getThumbnailUrl(), priceFrom, skus, collections,
                p.getPublishedAt()), p);
    }
}
