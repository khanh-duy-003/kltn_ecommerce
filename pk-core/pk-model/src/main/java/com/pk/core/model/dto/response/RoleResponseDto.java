package com.pk.core.model.dto.response;

import com.pk.core.model.entity.RoleEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/** Một vai trò (GET /admin/roles) kèm danh sách quyền chi tiết mà vai trò đó mang (rỗng nếu chưa biết). */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleResponseDto {

    private Long id;
    private String name;
    private List<String> permissions;

    public static RoleResponseDto from(RoleEntity r) {
        return new RoleResponseDto(r.getId(), r.getName(), List.of());
    }

    public static RoleResponseDto from(RoleEntity r, List<String> permissions) {
        return new RoleResponseDto(r.getId(), r.getName(), permissions);
    }
}
