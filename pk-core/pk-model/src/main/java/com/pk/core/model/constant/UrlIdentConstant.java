package com.pk.core.model.constant;

import lombok.experimental.UtilityClass;

/**
 * Đường dẫn API của pk-identity (AuthRest, SecurityConfig). Chuyển từ `identity.constant.UrlIdentity`
 * sang pk-model (2026-09-25, theo yêu cầu người dùng: "url của iden cũng sài chung luôn. không tách
 * riêng nữa. nhưng tạo 1 class cho riêng nó") - vẫn là 1 class riêng (không gộp lẫn vào các nhóm của
 * `UrlConstant`), nhưng đặt cùng chỗ với `UrlConstant` và dùng lại `UrlConstant.Common.BASE` thay vì
 * tự định nghĩa lại "/api" (tránh lặp hằng số).
 */
@UtilityClass
public class UrlIdentConstant {

    /** Endpoint đơn lẻ không thuộc nhóm nào. */
    @UtilityClass
    public static class Common {
        public final String ME = UrlConstant.Common.BASE + "/me";
    }

    /** AuthRest. */
    @UtilityClass
    public static class Auth {
        public final String BASE = UrlConstant.Common.BASE + "/auth";
    }

    /** StorefrontAuthRest: luồng SĐT + OTP theo spec FE (/storefront/auth/...), thêm 2026-10-06. Tách khỏi
     * {@link Auth} (/api/auth/...) để không đổi route cũ. */
    @UtilityClass
    public static class StorefrontAuth {
        public final String BASE = UrlConstant.Storefront.BASE + "/auth";
        /** Đường dẫn "thông tin tôi" theo spec FE (/storefront/me) - alias của Common.ME (/api/me), cùng handler. */
        public final String ME = UrlConstant.Storefront.BASE + "/me";
    }
}
