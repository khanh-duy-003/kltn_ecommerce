package com.pk.core.model.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/** POST /storefront/cart/cart: thêm vào giỏ (clearAll=false) hoặc thay toàn bộ giỏ bằng danh sách này (clearAll=true). */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartSyncRequestDto {

    /** true: xoá hết dòng hiện có trong giỏ rồi ghi lại theo items (đồng bộ); false/bỏ trống: cộng thêm vào giỏ. */
    private boolean clearAll;

    @NotNull
    @Valid
    private List<CartSyncItemRequestDto> items;
}
