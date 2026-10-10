package com.pk.core.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Kết quả upload: dùng `url` làm url cho media sản phẩm / banner / block CMS. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UploadResponseDto {

    /** Đường dẫn truy cập công khai (có tiền tố app.upload.public-base-url nếu cấu hình). */
    private String url;
    private String fileName;
    private String originalName;
    private long size;
    private String contentType;
    /** IMAGE | VIDEO. */
    private String type;
}
