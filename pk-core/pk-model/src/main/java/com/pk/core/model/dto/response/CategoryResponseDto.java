package com.pk.core.model.dto.response;

import com.pk.core.common.dto.BaseDto;
import com.pk.core.model.entity.CategoryEntity;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponseDto extends BaseDto {

    private Long id;
    private Long parentId;
    private String name;
    private String slug;
    private String description;
    private String imageUrl;
    private int sortOrder;

    public static CategoryResponseDto from(CategoryEntity c) {
        return BaseDto.of(new CategoryResponseDto(c.getId(), c.getParentId(), c.getName(), c.getSlug(),
                c.getDescription(), c.getImageUrl(), c.getSortOrder()), c);
    }

    // ---- FE ApiCategory: field tính toán, giữ nguyên field cũ ----

    public String getCode() {
        return slug;
    }

    public String getImage() {
        return imageUrl;
    }

    public List<String> getAncestorCategoryIds() {
        return List.of();
    }

    public List<String> getAncestorCategorySlugs() {
        return List.of();
    }

    public List<String> getAllowedAttributeIds() {
        return List.of();
    }

    public List<Object> getAllowedFilterAttributes() {
        return List.of();
    }
}
