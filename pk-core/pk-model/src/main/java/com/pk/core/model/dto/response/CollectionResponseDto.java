package com.pk.core.model.dto.response;

import com.pk.core.common.dto.BaseDto;
import com.pk.core.model.entity.CollectionEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CollectionResponseDto extends BaseDto {

    private Long id;
    private String name;
    private String slug;
    private String description;
    private String heroImageUrl;
    private String status;

    public static CollectionResponseDto from(CollectionEntity c) {
        return BaseDto.of(new CollectionResponseDto(c.getId(), c.getName(), c.getSlug(), c.getDescription(),
                c.getHeroImageUrl(), c.getStatus()), c);
    }
}
