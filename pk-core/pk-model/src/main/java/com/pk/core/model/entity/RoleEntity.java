package com.pk.core.model.entity;

import com.pk.core.model.constant.TableConstant;
import com.pk.core.common.entity.CreateEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import vn.com.unit.miragesql.miragesql.annotation.Column;
import vn.com.unit.miragesql.miragesql.annotation.PrimaryKey;
import vn.com.unit.miragesql.miragesql.annotation.PrimaryKey.GenerationType;
import vn.com.unit.miragesql.miragesql.annotation.Table;

@Getter
@Setter
@NoArgsConstructor
@Table(name = TableConstant.ROLES)
public class RoleEntity extends CreateEntity {

    public static final String ADMIN = "ADMIN";
    public static final String CUSTOMER = "CUSTOMER";

    @Id
    @PrimaryKey(generationType = GenerationType.SEQUENCE, generator = "roles_id_seq")
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    public RoleEntity(String name) {
        this.name = name;
    }
}
