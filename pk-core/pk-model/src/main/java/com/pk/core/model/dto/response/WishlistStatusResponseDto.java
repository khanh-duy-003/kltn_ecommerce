package com.pk.core.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Trạng thái yêu thích của một sản phẩm. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WishlistStatusResponseDto {

    private String productId;
    private boolean wishlisted;

    /** FE đọc `isWished`. */
    public boolean getIsWished() {
        return wishlisted;
    }
}
