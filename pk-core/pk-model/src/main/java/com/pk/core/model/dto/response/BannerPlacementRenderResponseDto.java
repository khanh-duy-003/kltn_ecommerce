package com.pk.core.model.dto.response;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/** Kết quả render một vị trí banner: thông tin vị trí + các banner ACTIVE theo thứ tự hiển thị. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BannerPlacementRenderResponseDto {

    private String code;
    private String name;
    private String displayType;
    private List<BannerResponseDto> banners;

    // ---- FE BannerPlacement {id, code, name, type, settings, slots[]}: field tính toán ----
    // Backend chưa có cấu hình slot/lưới (tọa độ, span) nên gom toàn bộ banner vào MỘT slot "main" theo thứ tự hiển thị.

    public String getId() {
        return code;
    }

    public String getType() {
        return displayType;
    }

    public List<Map<String, Object>> getSlots() {
        List<Map<String, Object>> items = new ArrayList<>();
        int i = 0;
        for (BannerResponseDto b : banners == null ? List.<BannerResponseDto>of() : banners) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("order_index", i++);
            item.put("banner", b);
            items.add(item);
        }
        Map<String, Object> layout = new LinkedHashMap<>();
        layout.put("order", 0);
        layout.put("x", 0);
        layout.put("y", 0);
        layout.put("width", 1);
        layout.put("height", 1);
        Map<String, Object> slot = new LinkedHashMap<>();
        slot.put("id", code + "_main");
        slot.put("slot_key", "main");
        slot.put("slot_type", items.size() > 1 ? "MULTIPLE_IMAGE" : "SINGLE_IMAGE");
        slot.put("layout_metadata", layout);
        slot.put("banners", items);
        return List.of(slot);
    }
}
