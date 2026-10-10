package com.pk.core.model.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    /** Chỉ dùng để dựng `page` (không xuất ra JSON ở dạng phẳng). */
    @JsonIgnore
    private String locale;
    @JsonIgnore
    private Map<String, Object> seo;

    public CmsStorefrontPageResponseDto(String id, String name, String slug, String status,
                                        List<CmsStorefrontBlockResponseDto> blocks) {
        this(id, name, slug, status, blocks, null, null);
    }

    // ---- FE PageResponse {page, layout, blocks}: field tính toán, giữ nguyên các field phẳng cũ ----

    public Map<String, Object> getPage() {
        Map<String, Object> seoOut = new LinkedHashMap<>();
        if (seo != null) {
            seoOut.putAll(seo);
        }
        seoOut.putIfAbsent("title", name);
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", id);
        m.put("name", name);
        m.put("slug", slug);
        m.put("locale", locale == null || locale.isBlank() ? "vi" : locale);
        m.put("seo", seoOut);
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
