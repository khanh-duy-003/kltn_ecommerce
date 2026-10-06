package com.pk.core.model.dto.response;

import com.pk.core.common.dto.UpdateDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

/** Block CMS theo spec FE: {id, type, sortOrder, config, targetSegment}. extends UpdateDto (CmsBlockEntity là
 * UpdateEntity - không có deleted_*); audit được copy tay trong CmsServiceImpl. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CmsBlockResponseDto extends UpdateDto {

    private String id;
    private String type;
    private int sortOrder;
    private Map<String, Object> config;
    private String targetSegment;
}
