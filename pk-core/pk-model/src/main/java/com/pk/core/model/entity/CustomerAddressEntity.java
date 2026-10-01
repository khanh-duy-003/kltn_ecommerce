package com.pk.core.model.entity;

import com.pk.core.model.constant.TableConstant;
import com.pk.core.common.entity.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import vn.com.unit.miragesql.miragesql.annotation.Column;
import vn.com.unit.miragesql.miragesql.annotation.PrimaryKey;
import vn.com.unit.miragesql.miragesql.annotation.PrimaryKey.GenerationType;
import vn.com.unit.miragesql.miragesql.annotation.Table;

/** Địa chỉ giao hàng của khách hàng (GET/POST/PUT/DELETE /storefront/me/addresses). Không có FK
 * từ orders trỏ tới bảng này - lúc đặt hàng, địa chỉ được SAO CHÉP (snapshot) sang các cột ship_*
 * của orders, nên sửa/xoá address ở đây không ảnh hưởng đơn đã đặt trước đó. */
@Getter
@Setter
@NoArgsConstructor
@Table(name = TableConstant.CUSTOMER_ADDRESSES)
public class CustomerAddressEntity extends BaseEntity {

    @Id
    @PrimaryKey(generationType = GenerationType.SEQUENCE, generator = "customer_addresses_id_seq")
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "recipient_name")
    private String recipientName;

    @Column(name = "phone")
    private String phone;

    @Column(name = "province")
    private String province;

    @Column(name = "district")
    private String district;

    @Column(name = "ward")
    private String ward;

    @Column(name = "address_line")
    private String addressLine;

    @Column(name = "is_default")
    private boolean isDefault;

    public CustomerAddressEntity(Long userId, String recipientName, String phone, String province,
                                  String district, String ward, String addressLine) {
        this.userId = userId;
        this.recipientName = recipientName;
        this.phone = phone;
        this.province = province;
        this.district = district;
        this.ward = ward;
        this.addressLine = addressLine;
    }
}
