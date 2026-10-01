package com.pk.core.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Body cho PUT /api/me (sửa hồ sơ - KHÔNG phải authen, chỉ đổi tên/email; đổi mật khẩu vẫn thuộc
 * phạm vi authen nên chưa làm). */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequestDto {

    @NotBlank
    @Size(max = 120)
    private String fullName;

    @NotBlank
    @Email
    @Size(max = 254)
    private String email;
}
