package com.pk.core.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Body cho PUT /api/me (sửa hồ sơ - KHÔNG phải authen, chỉ đổi tên/email; đổi mật khẩu vẫn thuộc
 * phạm vi authen nên chưa làm). SĐT không đổi ở đây vì là định danh đăng nhập - đổi SĐT cần xác thực
 * OTP (chưa làm). Email tuỳ chọn: bỏ trống/null = xoá email đã lưu. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequestDto {

    private String fullName;
    /** FE gửi firstName (+ lastName) thay cho fullName. */
    private String firstName;
    private String lastName;

    @Email
    @Size(max = 254)
    private String email;

    @NotBlank
    @Size(max = 120)
    public String getFullName() {
        if (fullName != null && !fullName.isBlank()) {
            return fullName;
        }
        String joined = ((lastName == null ? "" : lastName.trim()) + " " + (firstName == null ? "" : firstName.trim())).trim();
        return joined.isEmpty() ? null : joined;
    }
}
