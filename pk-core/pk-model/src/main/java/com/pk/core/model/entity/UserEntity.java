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

    /** Định danh đăng nhập: SĐT chuẩn hoá 10 số bắt đầu bằng 0 (PhoneUtil.normalize), UNIQUE, bắt buộc. */
    @Column(name = "phone")
    private String phone;

    /** Thông tin phụ TUỲ CHỌN (có thể null), không dùng để đăng nhập. Có giá trị thì luôn chữ thường, UNIQUE. */
    @Column(name = "email")
    private String email;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "enabled")
    private boolean enabled = true;

    /** Không phải cột: nạp từ bảng user_roles bằng RoleRepo.findByUserId. */
    @Transient
    private Set<RoleEntity> roles = new HashSet<>();

    /** email không nhận ở constructor (tuỳ chọn): gán bằng setEmail khi có. 3 tham số cố ý khác bản cũ
     * 4 tham số (email, hash, tên, phone) để chỗ nào còn gọi kiểu cũ sẽ lỗi biên dịch thay vì lặng lẽ
     * nhét nhầm giá trị (cả 4 đều là String). */
    public UserEntity(String phone, String passwordHash, String fullName) {
        this.phone = phone;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
    }

    public boolean hasRole(String roleName) {
        return roles.stream().anyMatch(r -> r.getName().equals(roleName));
    }
}
