package com.pk.core.model.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** 1 dòng trong request tạo đơn - không có API cart riêng (FE quản lý giỏ phía client, xem
 * document/09-tong-hop-api-fe.md mục D) nên client phải gửi kèm toàn bộ dòng hàng lúc đặt. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemRequestDto {

    @NotNull
    private Long skuId;

    @Min(1)
    private int quantity;
}
