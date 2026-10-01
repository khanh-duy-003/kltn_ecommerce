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
import vn.com.unit.miragesql.miragesql.annotation.Transient;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Table(name = TableConstant.USERS)
public class UserEntity extends BaseEntity {

    @Id
    @PrimaryKey(generationType = GenerationType.SEQUENCE, generator = "users_id_seq")
    @Column(name = "id")
    private Long id;

    /** Luôn lưu chữ thường. */
    @Column(name = "email")
    private String email;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "phone")
    private String phone;

    @Column(name = "enabled")
    private boolean enabled = true;

    /** Không phải cột: nạp từ bảng user_roles bằng RoleRepo.findByUserId. */
    @Transient
    private Set<RoleEntity> roles = new HashSet<>();

    public UserEntity(String email, String passwordHash, String fullName, String phone) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.phone = phone;
    }

    public boolean hasRole(String roleName) {
        return roles.stream().anyMatch(r -> r.getName().equals(roleName));
    }
}
