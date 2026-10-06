package com.pk.core.business.service.impl;

import lombok.RequiredArgsConstructor;

import com.pk.core.business.repository.RoleRepo;
import com.pk.core.business.repository.UserRepo;
import com.pk.core.business.service.AdminAccessService;
import com.pk.core.model.dto.response.AdminUserResponseDto;
import com.pk.core.model.dto.response.RoleResponseDto;
import com.pk.core.model.entity.RoleEntity;
import com.pk.core.model.entity.UserEntity;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminAccessServiceImpl implements AdminAccessService {

    private final UserRepo users;
    private final RoleRepo roles;

    @Transactional(readOnly = true)
    @Override
    public List<AdminUserResponseDto> listUsers() {
        List<UserEntity> all = new ArrayList<>();
        for (UserEntity u : users.findAll(Sort.unsorted())) {
            if (u.getDeletedDate() == null) {
                all.add(u);
            }
        }
        all.sort(Comparator.comparing(UserEntity::getId));
        List<AdminUserResponseDto> result = new ArrayList<>();
        for (UserEntity u : all) {
            List<String> roleNames = roles.findByUserId(u.getId()).stream().map(RoleEntity::getName).sorted().toList();
            result.add(AdminUserResponseDto.from(u, roleNames));
        }
        return result;
    }

    @Transactional(readOnly = true)
    @Override
    public List<RoleResponseDto> listRoles() {
        List<RoleEntity> all = new ArrayList<>();
        for (RoleEntity r : roles.findAll(Sort.unsorted())) {
            all.add(r);
        }
        all.sort(Comparator.comparing(RoleEntity::getId));
        return all.stream().map(RoleResponseDto::from).toList();
    }
}
