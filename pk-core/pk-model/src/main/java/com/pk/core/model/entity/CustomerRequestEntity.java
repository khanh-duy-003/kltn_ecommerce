package com.pk.core.model.entity;

import com.pk.core.common.entity.UpdateEntity;
import com.pk.core.model.constant.TableConstant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import vn.com.unit.miragesql.miragesql.annotation.Column;
import vn.com.unit.miragesql.miragesql.annotation.PrimaryKey;
import vn.com.unit.miragesql.miragesql.annotation.PrimaryKey.GenerationType;
import vn.com.unit.miragesql.miragesql.annotation.Table;

/**
 * Yêu cầu khách hàng (spec FE nhóm Customer Request): báo khi có hàng lại, hỗ trợ đơn hàng, đăng ký nhận tin. Một bảng
 * chung, phân loại bằng {@code type}. Không xoá (chỉ đổi trạng thái) nên extends UpdateEntity. Các cột *Snapshot chụp lại
 * tên/ảnh/biến thể sản phẩm lúc gửi.
 */
@Getter
@Setter
@NoArgsConstructor
@Table(name = TableConstant.CUSTOMER_REQUESTS)
public class CustomerRequestEntity extends UpdateEntity {

    public static final String BACK_IN_STOCK = "BACK_IN_STOCK";
    public static final String ORDER_SUPPORT = "ORDER_SUPPORT";
    public static final String NEWSLETTER = "NEWSLETTER";

    public static final String PHONE = "PHONE";
    public static final String EMAIL = "EMAIL";

    public static final String NEW = "NEW";
    public static final String COMPLETED = "COMPLETED";

    public static final String ORDER = "ORDER";
    public static final String PRE_ORDER = "PRE_ORDER";

    @Id
    @PrimaryKey(generationType = GenerationType.SEQUENCE, generator = "customer_requests_id_seq")
    @Column(name = "id")
    private Long id;

    @Column(name = "type")
    private String type;

    @Column(name = "contact_channel")
    private String contactChannel;

    @Column(name = "contact_value")
    private String contactValue;

    @Column(name = "status")
    private String status = NEW;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "sku_id")
    private Long skuId;

    @Column(name = "sku_code")
    private String skuCode;

    @Column(name = "product_name_snapshot")
    private String productNameSnapshot;

    @Column(name = "product_image_url_snapshot")
    private String productImageUrlSnapshot;

    @Column(name = "variant_text_snapshot")
    private String variantTextSnapshot;

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "order_code")
    private String orderCode;

    @Column(name = "order_type")
    private String orderType;

    public CustomerRequestEntity(String type, String contactChannel, String contactValue) {
        this.type = type;
        this.contactChannel = contactChannel;
        this.contactValue = contactValue;
    }
}
