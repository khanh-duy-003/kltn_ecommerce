package com.pk.core.business.web;

import org.springframework.validation.BindingResult;

import com.pk.core.common.web.BaseRes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;

/**
 * Dựng BaseRes cho response lỗi. request/response có thể null khi gọi từ nơi không có
 * HttpServletResponse tiện dụng để set status (AbstractRest/GlobalExceptionHandler luôn truyền,
 * còn lại có thể bỏ null nếu không cần set status thủ công).
 */
public interface RestErrorHandle {

    BaseRes handleException(Exception ex, HttpServletRequest request, HttpServletResponse response);

    BaseRes handleValidation(BindingResult bindingResult, HttpServletRequest request, HttpServletResponse response);

    BaseRes handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request, HttpServletResponse response);
}
