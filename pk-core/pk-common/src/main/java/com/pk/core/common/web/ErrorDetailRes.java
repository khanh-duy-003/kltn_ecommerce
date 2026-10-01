package com.pk.core.common.web;

import lombok.Getter;
import lombok.Setter;

/**
 * Chi tiết 1 lỗi trong BaseRes.errors - phỏng theo ErrorDetailRes của boxchatSocket, có bổ sung
 * field `code` (bản gốc chỉ để sẵn dòng comment, không dùng) để FE phân biệt được loại lỗi nghiệp vụ
 * (khớp ErrorCode ở pk-common) hoặc loại lỗi validate (annotation Bean Validation, VD "NotBlank").
 */
@Getter
@Setter
public class ErrorDetailRes {

    /** message: nội dung lỗi cụ thể (đã dịch nếu có i18n). */
    private String message;

    /** code: mã lỗi - ErrorCode cho lỗi nghiệp vụ, hoặc tên annotation validate cho lỗi field. */
    private String code;

    /** objectName: tên object chứa field lỗi, optional (dùng cho lỗi validate). */
    private String objectName;

    /** field: tên field lỗi, optional (dùng cho lỗi validate). */
    private String field;

    /** rejectValue: giá trị bị từ chối, optional (dùng cho lỗi validate). */
    private Object rejectValue;
}
