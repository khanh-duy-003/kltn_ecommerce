package com.pk.core.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Phản hồi chỉ có thông điệp (spec FE: MessageResponse {success, message}). */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponseDto {

    private boolean success;

    private String message;

    public static MessageResponseDto ok(String message) {
        return new MessageResponseDto(true, message);
    }
}
