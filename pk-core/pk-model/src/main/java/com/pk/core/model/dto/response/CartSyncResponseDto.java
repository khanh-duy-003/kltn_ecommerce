package com.pk.core.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/** Kết quả thêm/đồng bộ/gộp giỏ hàng. guestId chỉ có với khách vãng lai (FE lưu lại, gửi ở header X-Guest-Cart-Id). */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartSyncResponseDto {

    /** false nếu có dòng không thêm được hoặc bị giảm số lượng (xem reason/message); giỏ vẫn được cập nhật phần còn lại. */
    private boolean success;
    private String guestId;
    private List<CartLineResponseDto> items;
    /** NOT_FOUND / OUT_OF_STOCK / INSUFFICIENT_STOCK; null khi success. */
    private String reason;
    private String message;
}
