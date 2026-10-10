package com.pk.core.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    // ---- FE PageResponse {page, layout, blocks}: field tính toán, giữ nguyên các field phẳng cũ ----

    public Map<String, Object> getPage() {
        Map<String, Object> seo = new LinkedHashMap<>();
        seo.put("title", name);
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", id);
        m.put("name", name);
        m.put("slug", slug);
        m.put("locale", "vi");
        m.put("seo", seo);
        return m;
    }

    /** Layout trình bày cố định (backend chưa có hệ thống layout CMS). */
    public Map<String, Object> getLayout() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", "default");
        m.put("name", "Default");
        m.put("targetDevice", "ALL");
        m.put("versionId", "1");
        m.put("versionName", "v1");
        return m;
    }
}
