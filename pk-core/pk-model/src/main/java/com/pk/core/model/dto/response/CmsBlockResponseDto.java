package com.pk.core.model.dto.response;

import com.pk.core.common.dto.UpdateDto;
import com.pk.core.model.entity.CmsBlockEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** extends UpdateDto (không phải BaseDto) - khớp CmsBlockEntity extends UpdateEntity (bảng
 * cms_blocks không có deleted_id/deleted_date), gọi tay copyAudit() giống ProductAttributeResponseDto. */
@Getter
@Setter
@NoArgsConstructor
public class CmsBlockResponseDto extends UpdateDto {

    private Long id;
    private Long pageId;
    private String type;
    private int sortOrder;
    private String data;

    public static CmsBlockResponseDto from(CmsBlockEntity e) {
        CmsBlockResponseDto dto = new CmsBlockResponseDto();
        dto.setId(e.getId());
        dto.setPageId(e.getPageId());
        dto.setType(e.getType());
        dto.setSortOrder(e.getSortOrder());
        dto.setData(e.getData());
        dto.copyAudit(e);
        return dto;
    }
}
