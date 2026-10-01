package com.pk.core.business.config;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.pk.core.business.web.RestErrorHandle;
import com.pk.core.common.web.BaseRes;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

/**
 * Lưới an toàn cho lỗi xảy ra NGOÀI try/catch của controller. Từ khi controller tự bắt lỗi nghiệp vụ
 * qua AbstractRest/RestSuccessHandle/RestErrorHandle (xem package business.web, phỏng theo
 * boxchatSocket), handler này chỉ còn 3 việc:
 *  - MethodArgumentNotValidException: lỗi @Valid trên @RequestBody, ném RA TRƯỚC khi vào thân
 *    method nên controller không try/catch được.
 *  - handleExceptionInternal: các lỗi MVC chuẩn khác (405, 415, JSON hỏng, thiếu param...).
 *  - @ExceptionHandler(Exception.class): lưới cuối, phòng trường hợp hiếm hoi lọt ra ngoài try/catch.
 * Tất cả đều build ra BaseRes qua RestErrorHandle để hình dạng response giống hệt controller.
 */
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private final RestErrorHandle restErrorHandle;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseRes> handleAny(Exception ex, HttpServletRequest request) {
        BaseRes body = restErrorHandle.handleException(ex, request, null);
        return ResponseEntity.status(body.getStatus()).body(body);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                    HttpHeaders headers, HttpStatusCode status,
                                                                    WebRequest request) {
        HttpServletRequest servletRequest = request instanceof ServletWebRequest sw ? sw.getRequest() : null;
        BaseRes body = restErrorHandle.handleValidation(ex.getBindingResult(), servletRequest, null);
        return ResponseEntity.status(body.getStatus()).headers(headers).body(body);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
                                                              HttpStatusCode statusCode, WebRequest request) {
        BaseRes res = new BaseRes(statusCode.value(), ex.getMessage(), "Lỗi yêu cầu");
        return ResponseEntity.status(statusCode).headers(headers).body(res);
    }
}
