package com.pk.core.test.business;

import com.fasterxml.jackson.databind.JsonNode;
import com.pk.core.model.constant.UrlConstant;
import com.pk.core.model.constant.admin.UrlAdminConstant;
import com.pk.core.test.common.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Upload ảnh (admin) -> URL công khai tải lại được; từ chối file giả mạo (HTML đổi đuôi .png) và thiếu quyền. */
class AdminUploadIT extends IntegrationTestBase {

    private static final String UPLOAD = UrlConstant.Common.API + UrlConstant.Common.VERSION + UrlAdminConstant.Catalog.UPLOAD;
    private static final byte[] PNG = {(byte) 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A, 0, 0, 0, 0x0D, 'I', 'H', 'D', 'R'};

    @Test
    void adminUploadsImageAndItIsServedPublicly() throws Exception {
        JsonNode res = body(mvc.perform(multipart(UPLOAD).file(new MockMultipartFile("file", "nhan.png", "image/png", PNG))
                        .header("Authorization", bearer(adminAccessToken())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.type").value("IMAGE"))
                .andExpect(jsonPath("$.data.contentType").value("image/png"))
                .andExpect(jsonPath("$.data.originalName").value("nhan.png")));
        String url = res.get("url").asText();
        org.junit.jupiter.api.Assertions.assertTrue(url.endsWith(".png"));
        // Tải lại KHÔNG cần đăng nhập.
        mvc.perform(get(url)).andExpect(status().isOk());
    }

    @Test
    void fakeImageAndMissingFileAreRejected() throws Exception {
        String admin = bearer(adminAccessToken());
        mvc.perform(multipart(UPLOAD).file(new MockMultipartFile("file", "x.png", "image/png",
                        "<html><script>alert(1)</script></html>".getBytes(StandardCharsets.UTF_8)))
                        .header("Authorization", admin))
                .andExpect(status().isBadRequest());
        mvc.perform(multipart(UPLOAD).file(new MockMultipartFile("file", "empty.png", "image/png", new byte[0]))
                        .header("Authorization", admin))
                .andExpect(status().isBadRequest());
    }

    @Test
    void uploadRequiresAdminToken() throws Exception {
        mvc.perform(multipart(UPLOAD).file(new MockMultipartFile("file", "a.png", "image/png", PNG)))
                .andExpect(status().isUnauthorized());
        mvc.perform(multipart(UPLOAD).file(new MockMultipartFile("file", "a.png", "image/png", PNG))
                        .header("Authorization", bearer(customerAccessToken())))
                .andExpect(status().isForbidden());
    }
}
