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

/** Một dòng giỏ hàng: 1 SKU + số lượng (mỗi SKU tối đa 1 dòng trong 1 giỏ). FE gọi SKU là "variation". */
@Getter
@Setter
@NoArgsConstructor
@Table(name = TableConstant.CART_ITEMS)
public class CartItemEntity extends UpdateEntity {

    @Id
    @PrimaryKey(generationType = GenerationType.SEQUENCE, generator = "cart_items_id_seq")
    @Column(name = "id")
    private Long id;

    @Column(name = "cart_id")
    private Long cartId;

    @Column(name = "sku_id")
    private Long skuId;

    @Column(name = "quantity")
    private int quantity;

    public CartItemEntity(Long cartId, Long skuId, int quantity) {
        this.cartId = cartId;
        this.skuId = skuId;
        this.quantity = quantity;
    }
}
