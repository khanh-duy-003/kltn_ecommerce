package com.pk.core.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SetItemResponseDto {

    private Long productId;
    private String productName;
    private String thumbnailUrl;
    private String slotName;
    private boolean isKeyPiece;
    private int sortOrder;
}
