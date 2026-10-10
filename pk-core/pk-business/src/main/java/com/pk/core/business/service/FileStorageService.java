package com.pk.core.business.service;

import com.pk.core.model.dto.response.UploadResponseDto;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    /** Lưu file ảnh/video hợp lệ (kiểm tra theo nội dung thật, không tin tên/Content-Type do client gửi). */
    UploadResponseDto store(MultipartFile file);
}
