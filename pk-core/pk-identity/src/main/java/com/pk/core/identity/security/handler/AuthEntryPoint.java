package com.pk.core.identity.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pk.core.common.web.BaseRes;
import com.pk.core.business.web.RestErrorHandle;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/** 401: chưa đăng nhập / token sai / token hết hạn -> JSON BaseRes (qua RestErrorHandle) thay vì
 * trang lỗi mặc định - cách nối dây phỏng theo DefaultAuthenticationEntryPoint của boxchatSocket. */
@Component
@RequiredArgsConstructor
public class AuthEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;
    private final RestErrorHandle restErrorHandle;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException ex) throws IOException {
        // Tính body (restErrorHandle tự set response.setStatus bên trong) TRƯỚC khi mở writer,
        // để tránh trường hợp status set sau khi writer đã lấy ra.
        BaseRes body = restErrorHandle.handleException(ex, request, response);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        objectMapper.writeValue(response.getWriter(), body);
    }
}
