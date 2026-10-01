package com.pk.core.model.dto.response;

import com.pk.core.common.dto.BaseDto;
import com.pk.core.model.entity.CmsPageEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CmsPageResponseDto extends BaseDto {

    private Long id;
    private String slug;
    private String title;
    private String status;
    private List<CmsBlockResponseDto> blocks;

    public static CmsPageResponseDto from(CmsPageEntity e, List<CmsBlockResponseDto> blocks) {
        return BaseDto.of(new CmsPageResponseDto(e.getId(), e.getSlug(), e.getTitle(), e.getStatus(), blocks), e);
    }
}
