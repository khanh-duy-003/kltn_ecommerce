package com.pk.core.model.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/** Body cho POST /admin/catalog/collections/{collectionId}/products - thêm sản phẩm vào bộ sưu tập. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminCollectionProductsRequestDto {
    @NotEmpty(message = "Danh sách sản phẩm không được để trống")
    private List<Long> productIds;
}
