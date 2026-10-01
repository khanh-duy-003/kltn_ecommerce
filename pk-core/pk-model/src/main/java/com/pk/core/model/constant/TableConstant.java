package com.pk.core.model.constant;

/** Tên các bảng (dùng trong @Table của entity). Tên cột vẫn khai báo trực tiếp trong entity.
 * Cập nhật 2026-09-29 theo schema mới (V1__init.sql thiết kế lại) - thêm bảng address/order/payment/
 * voucher/promotion (một số bảng chưa có entity tương ứng, để sẵn hằng khi làm tới). */
public final class TableConstant {

    // Identity
    public static final String ROLES = "roles";
    public static final String USERS = "users";
    public static final String REFRESH_TOKENS = "refresh_tokens";

    // Địa chỉ
    public static final String CUSTOMER_ADDRESSES = "customer_addresses";

    // Catalog
    public static final String CATEGORIES = "categories";
    public static final String COLLECTIONS = "collections";
    public static final String PRODUCTS = "products";
    public static final String PRODUCT_SKUS = "product_skus";

    // Đơn hàng (chưa có entity, để sẵn hằng)
    public static final String ORDERS = "orders";
    public static final String ORDER_ITEMS = "order_items";
    public static final String ORDER_TIMELINE = "order_timeline";

    // Thanh toán (chưa có entity, để sẵn hằng)
    public static final String PAYMENTS = "payments";

    // Voucher & khuyến mãi (chưa có entity, để sẵn hằng)
    public static final String VOUCHERS = "vouchers";
    public static final String PROMOTIONS = "promotions";

    // Admin - Catalog: thuộc tính sản phẩm (V2__admin_extensions.sql, mới 2026-09-29)
    public static final String PRODUCT_ATTRIBUTES = "product_attributes";

    // Admin - CMS (V2__admin_extensions.sql, mới 2026-09-29, chưa có entity, để sẵn hằng)
    public static final String CMS_PAGES = "cms_pages";
    public static final String CMS_BLOCKS = "cms_blocks";

    // Admin - Badge (V2__admin_extensions.sql, mới 2026-09-29, chưa có entity, để sẵn hằng)
    public static final String BADGE_TEMPLATES = "badge_templates";
    public static final String BADGE_FLOW = "badge_flow";

    private TableConstant() {
    }
}
