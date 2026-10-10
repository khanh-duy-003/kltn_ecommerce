package com.pk.core.business.service.impl;

import com.pk.core.business.service.FileStorageService;
import com.pk.core.common.exception.BusinessException;
import com.pk.core.common.exception.ErrorCode;
import com.pk.core.model.constant.UrlConstant;
import com.pk.core.model.dto.response.UploadResponseDto;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Lưu file lên đĩa cục bộ (mặc định D:/pk-uploads, đổi bằng biến môi trường UPLOAD_DIR). Tên file do server sinh (UUID) và
 * phần mở rộng suy ra từ NỘI DUNG file (magic bytes) nên không thể upload HTML/script giả làm ảnh.
 */
@Service
public class LocalFileStorageService implements FileStorageService {

    private enum Kind {
        PNG("png", "image/png", "IMAGE"), JPEG("jpg", "image/jpeg", "IMAGE"), GIF("gif", "image/gif", "IMAGE"),
        WEBP("webp", "image/webp", "IMAGE"), MP4("mp4", "video/mp4", "VIDEO"), WEBM("webm", "video/webm", "VIDEO");

        final String ext;
        final String contentType;
        final String type;

        Kind(String ext, String contentType, String type) {
            this.ext = ext;
            this.contentType = contentType;
            this.type = type;
        }
    }

    @Value("${app.upload.dir:D:/pk-uploads}")
    private String dir;

    @Value("${app.upload.public-base-url:}")
    private String publicBaseUrl;

    @Value("${app.upload.max-bytes:10485760}")
    private long maxBytes;

    private Path root;

    @PostConstruct
    void init() throws IOException {
        root = Paths.get(dir).toAbsolutePath().normalize();
        Files.createDirectories(root);
    }

    @Override
    public UploadResponseDto store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw BusinessException.badRequest(ErrorCode.VALIDATION_FAILED, "Chưa chọn file (field `file`)");
        }
        if (file.getSize() > maxBytes) {
            throw BusinessException.badRequest(ErrorCode.VALIDATION_FAILED,
                    "File quá lớn (tối đa " + (maxBytes / 1024 / 1024) + "MB)");
        }
        byte[] head = new byte[16];
        int read;
        try (InputStream in = file.getInputStream()) {
            read = in.readNBytes(head, 0, head.length);
        } catch (IOException ex) {
            throw BusinessException.badRequest(ErrorCode.VALIDATION_FAILED, "Không đọc được file");
        }
        Kind kind = sniff(head, read);
        if (kind == null) {
            throw BusinessException.badRequest(ErrorCode.VALIDATION_FAILED,
                    "Định dạng không được hỗ trợ (cho phép: png, jpg, gif, webp, mp4, webm)");
        }
        LocalDate today = LocalDate.now();
        String relative = today.getYear() + "/" + String.format("%02d", today.getMonthValue()) + "/"
                + UUID.randomUUID() + "." + kind.ext;
        Path target = root.resolve(relative).normalize();
        if (!target.startsWith(root)) {
            throw BusinessException.badRequest(ErrorCode.VALIDATION_FAILED, "Đường dẫn lưu không hợp lệ");
        }
        try {
            Files.createDirectories(target.getParent());
            try (InputStream in = file.getInputStream()) {
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Không ghi được file upload", ex);
        }
        String base = publicBaseUrl == null ? "" : publicBaseUrl.trim().replaceAll("/+$", "");
        String url = base + UrlConstant.Common.API + UrlConstant.Common.VERSION + UrlConstant.Storefront.FILES + "/" + relative;
        String original = file.getOriginalFilename() == null ? null : Paths.get(file.getOriginalFilename()).getFileName().toString();
        return new UploadResponseDto(url, relative.substring(relative.lastIndexOf('/') + 1), original, file.getSize(),
                kind.contentType, kind.type);
    }

    private static Kind sniff(byte[] b, int n) {
        if (n >= 8 && (b[0] & 0xFF) == 0x89 && b[1] == 'P' && b[2] == 'N' && b[3] == 'G') {
            return Kind.PNG;
        }
        if (n >= 3 && (b[0] & 0xFF) == 0xFF && (b[1] & 0xFF) == 0xD8 && (b[2] & 0xFF) == 0xFF) {
            return Kind.JPEG;
        }
        if (n >= 6 && new String(b, 0, 4, StandardCharsets.ISO_8859_1).equals("GIF8")) {
            return Kind.GIF;
        }
        if (n >= 12 && new String(b, 0, 4, StandardCharsets.ISO_8859_1).equals("RIFF")
                && new String(b, 8, 4, StandardCharsets.ISO_8859_1).equals("WEBP")) {
            return Kind.WEBP;
        }
        if (n >= 12 && new String(b, 4, 4, StandardCharsets.ISO_8859_1).equals("ftyp")) {
            return Kind.MP4;
        }
        if (n >= 4 && (b[0] & 0xFF) == 0x1A && (b[1] & 0xFF) == 0x45 && (b[2] & 0xFF) == 0xDF && (b[3] & 0xFF) == 0xA3) {
            return Kind.WEBM;
        }
        return null;
    }
}
