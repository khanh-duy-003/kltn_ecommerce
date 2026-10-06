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

/** Giỏ hàng: của khách đăng nhập (userId) HOẶC khách vãng lai (guestId) - đúng một trong hai được điền. */
@Getter
@Setter
@NoArgsConstructor
@Table(name = TableConstant.CARTS)
public class CartEntity extends UpdateEntity {

    @Id
    @PrimaryKey(generationType = GenerationType.SEQUENCE, generator = "carts_id_seq")
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "guest_id")
    private String guestId;

    public static CartEntity forUser(Long userId) {
        CartEntity c = new CartEntity();
        c.userId = userId;
        c.setCreatedId(userId);
        return c;
    }

    public static CartEntity forGuest(String guestId) {
        CartEntity c = new CartEntity();
        c.guestId = guestId;
        return c;
    }
}
