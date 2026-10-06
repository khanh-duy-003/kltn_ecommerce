package com.pk.core.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/** GET /storefront/cms/pages/{slug}: {id, name, slug, status, blocks[]} - chỉ trang PUBLISHED, block đã sắp theo sortOrder. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CmsStorefrontPageResponseDto {

    private String id;
    private String name;
    private String slug;
    private String status;
    private List<CmsStorefrontBlockResponseDto> blocks;
}
