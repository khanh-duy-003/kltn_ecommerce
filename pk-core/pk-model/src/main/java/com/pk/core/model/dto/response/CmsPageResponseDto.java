package com.pk.core.model.dto.response;

import com.pk.core.common.dto.BaseDto;
import com.pk.core.model.entity.CmsPageEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/** Trang CMS cho admin theo spec FE: {id, name, slug, status, blocks}. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CmsPageResponseDto extends BaseDto {

    private String id;
    private String name;
    private String slug;
    private String status;
    private List<CmsBlockResponseDto> blocks;
    private String locale;
    private boolean active;
    private java.util.Map<String, Object> seo;

    public static CmsPageResponseDto from(CmsPageEntity e, List<CmsBlockResponseDto> blocks, java.util.Map<String, Object> seo) {
        return BaseDto.of(new CmsPageResponseDto(String.valueOf(e.getId()), e.getTitle(), e.getSlug(), e.getStatus(),
                blocks, e.getLocale(), e.isActive(), seo), e);
    }

    public boolean getIsActive() {
        return active;
    }
}
