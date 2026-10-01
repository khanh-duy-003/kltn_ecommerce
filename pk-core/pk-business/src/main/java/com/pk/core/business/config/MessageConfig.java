package com.pk.core.business.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.List;
import java.util.Locale;

/**
 * i18n cho thông báo lỗi (BaseRes.message / BaseRes.errors[].message). File dịch:
 * business/resources/i18n/messages_vi.properties và messages_en.properties, key = ErrorCode.
 * Ngôn ngữ chọn theo header Accept-Language của client (mặc định vi nếu không gửi hoặc gửi ngôn ngữ
 * khác); RestErrorHandleImpl (business.web) dùng MessageSource này để dịch trước khi trả BaseRes,
 * cho cả lỗi bắt trong try/catch của controller lẫn lỗi framework qua GlobalExceptionHandler.
 * LƯU Ý: pk-identity không có MessageSource riêng (xem pk-identity/web/RestErrorHandleImpl) nên
 * AuthEntryPoint/AccessDeniedHandlerImpl và lỗi ném trong AuthRest hiện vẫn cố định tiếng Việt.
 */
@Configuration
public class MessageConfig {

    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource source = new ResourceBundleMessageSource();
        source.setBasenames("i18n/messages");
        source.setDefaultEncoding("UTF-8");
        source.setFallbackToSystemLocale(false);
        source.setUseCodeAsDefaultMessage(false);
        return source;
    }

    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();
        resolver.setDefaultLocale(new Locale("vi"));
        resolver.setSupportedLocales(List.of(new Locale("vi"), Locale.ENGLISH));
        return resolver;
    }
}
