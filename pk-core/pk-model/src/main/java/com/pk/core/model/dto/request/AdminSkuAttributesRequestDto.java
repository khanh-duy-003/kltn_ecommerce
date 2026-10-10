package com.pk.core.model.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/** PUT .../variants/{skuId}/attributes: thay TOÀN BỘ thuộc tính của SKU ([] = xoá hết). */
@Getter
@Setter
@NoArgsConstructor
public class AdminSkuAttributesRequestDto {

    @NotNull
    @Valid
    private List<Item> values;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Item {
        /** Xác định thuộc tính bằng id hoặc code (một trong hai). */
        private Long attributeId;
        private String attributeCode;
        /** TEXT / SELECT: dùng `value`. MULTISELECT: dùng `values` (hoặc `value` nối bằng "||"). */
        private String value;
        private List<String> values;
    }
}
