package com.pk.core.business.config;

import com.pk.core.model.constant.UrlConstant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/** Phục vụ file đã upload: GET /api/v1/storefront/files/** -> thư mục app.upload.dir (công khai, chỉ đọc). */
@Configuration
public class UploadWebConfig implements WebMvcConfigurer {

    private final String dir;

    public UploadWebConfig(@Value("${app.upload.dir:D:/pk-uploads}") String dir) {
        this.dir = dir;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path root = Paths.get(dir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(root);
        } catch (java.io.IOException ex) {
            throw new IllegalStateException("Không tạo được thư mục upload: " + root, ex);
        }
        String location = root.toUri().toString();
        if (!location.endsWith("/")) {
            location += "/";
        }
        registry.addResourceHandler(UrlConstant.Common.API + UrlConstant.Common.VERSION + UrlConstant.Storefront.FILES + "/**")
                .addResourceLocations(location);
    }
}
