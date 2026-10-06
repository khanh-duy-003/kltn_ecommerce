package com.pk.core.business.repository;

import com.pk.core.model.entity.UserEntity;

import org.springframework.data.repository.query.Param;

import vn.com.unit.springframework.data.mirage.repository.query.Modifying;

import java.util.List;

public interface UserRepo extends PkRepo<UserEntity, Long> {

    /** Tra cứu khi đăng nhập (phone phải là dạng chuẩn hoá của PhoneUtil). Trả null nếu không có. */
    UserEntity findByPhone(@Param("phone") String phone);

    Long countByPhone(@Param("phone") String phone);

    /** Trả null nếu không có. Email là cột phụ, không dùng để đăng nhập. */
    UserEntity findByEmail(@Param("email") String email);

    Long countByEmail(@Param("email") String email);

    /** Gán role cho user (bảng user_roles). */
    @Modifying
    int addRole(@Param("userId") Long userId, @Param("roleId") Long roleId);

    /** Xoá hết vai trò của user (dùng trước khi ghi lại danh sách vai trò mới lúc gán vai trò). */
    @Modifying
    int removeAllRoles(@Param("userId") Long userId);

    /** Admin mục N (GET /admin/customers) - chỉ user có role CUSTOMER (loại trừ tài khoản ADMIN),
     * lọc rỗng = trả tất cả (cùng quy ước ProductRepo.searchAdmin). */
    List<UserEntity> searchCustomers(@Param("keyword") String keyword);
}
