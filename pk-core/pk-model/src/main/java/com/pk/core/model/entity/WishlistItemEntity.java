package com.pk.core.model.entity;

import com.pk.core.common.entity.CreateEntity;
import com.pk.core.model.constant.TableConstant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import vn.com.unit.miragesql.miragesql.annotation.Column;
import vn.com.unit.miragesql.miragesql.annotation.PrimaryKey;
import vn.com.unit.miragesql.miragesql.annotation.PrimaryKey.GenerationType;
import vn.com.unit.miragesql.miragesql.annotation.Table;

/** Sản phẩm yêu thích của một khách đăng nhập (mỗi cặp khách + sản phẩm tối đa 1 dòng). */
@Getter
@Setter
@NoArgsConstructor
@Table(name = TableConstant.WISHLIST_ITEMS)
public class WishlistItemEntity extends CreateEntity {

    @Id
    @PrimaryKey(generationType = GenerationType.SEQUENCE, generator = "wishlist_items_id_seq")
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "product_id")
    private Long productId;

    public WishlistItemEntity(Long userId, Long productId) {
        this.userId = userId;
        this.productId = productId;
        setCreatedId(userId);
    }
}
