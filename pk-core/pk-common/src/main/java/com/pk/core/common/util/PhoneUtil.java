package com.pk.core.common.util;

/**
 * Chuẩn hoá số điện thoại Việt Nam về MỘT dạng duy nhất để lưu DB và tra cứu: 10 chữ số, bắt đầu
 * bằng 0 (vd. {@code 0901234567}). SĐT là định danh đăng nhập (users.phone UNIQUE), nên "0901234567",
 * "+84901234567", "84901234567" và "+84 901 234 567" phải cùng quy về một giá trị - nếu không, một
 * người đăng ký được nhiều tài khoản bằng cùng một số.
 */
public final class PhoneUtil {

    /** Dạng chuẩn lưu DB (khớp CHECK ck_users_phone_format). */
    public static final String CANONICAL_REGEX = "^0\\d{9}$";

    /** Dạng người dùng được phép nhập (trước khi chuẩn hoá): 0xxxxxxxxx, 84xxxxxxxxx hoặc +84xxxxxxxxx. */
    public static final String INPUT_REGEX = "^(\\+?84|0)\\d{9}$";

    private PhoneUtil() {
    }

    /**
     * Bỏ khoảng trắng/dấu chấm/gạch/ngoặc rồi đổi đầu số quốc tế 84/+84 về 0. Không ném lỗi: chuỗi
     * rác trả về dạng đã làm sạch (không khớp {@link #CANONICAL_REGEX}) để tầng gọi tự quyết định
     * (đăng nhập thì coi như không tìm thấy tài khoản). null giữ nguyên null.
     */
    public static String normalize(String raw) {
        if (raw == null) {
            return null;
        }
        String s = raw.replaceAll("[\\s.\\-()]", "");
        if (s.startsWith("+84")) {
            return "0" + s.substring(3);
        }
        if (s.startsWith("84") && s.length() == 11) {
            return "0" + s.substring(2);
        }
        return s;
    }

    public static boolean isValid(String canonical) {
        return canonical != null && canonical.matches(CANONICAL_REGEX);
    }
}
