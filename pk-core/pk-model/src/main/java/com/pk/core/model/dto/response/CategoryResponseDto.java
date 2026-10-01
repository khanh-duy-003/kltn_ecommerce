package com.pk.core.model.dto.response;

import com.pk.core.common.dto.BaseDto;
import com.pk.core.model.entity.CategoryEntity;
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
}
