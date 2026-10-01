package com.pk.core.common.exception;

/**
 * 404: entity không tồn tại. Truyền cả tên tài nguyên tiếng Việt và tiếng Anh (resourceNameVi,
 * resourceNameEn) để pk-business dịch NOT_FOUND theo Accept-Language; message mặc định (khi
 * không dịch được) luôn là câu tiếng Việt.
 */
public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String resourceNameVi, String resourceNameEn, Object key) {
        super(404, ErrorCode.NOT_FOUND, resourceNameVi + " không tồn tại: " + key, resourceNameVi, resourceNameEn, key);
    }
}
