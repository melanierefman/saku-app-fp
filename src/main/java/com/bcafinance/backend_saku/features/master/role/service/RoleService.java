package com.bcafinance.backend_saku.features.master.role.service;

import com.bcafinance.backend_saku.core.entity.Menu;
import com.bcafinance.backend_saku.core.entity.Permission;
import com.bcafinance.backend_saku.core.entity.Role;
import com.bcafinance.backend_saku.core.entity.RolePermission;
import com.bcafinance.backend_saku.core.exception.BussinessRuleException;
import com.bcafinance.backend_saku.core.repository.MenuRepository;
import com.bcafinance.backend_saku.core.repository.PermissionRepository;
import com.bcafinance.backend_saku.core.repository.RolePermissionRepository;
import com.bcafinance.backend_saku.core.repository.RoleRepository;
import com.bcafinance.backend_saku.features.master.permission.dto.PermissionResponse;
import com.bcafinance.backend_saku.features.master.role.dto.RoleDetailResponse;
import com.bcafinance.backend_saku.features.master.role.dto.RoleRequest;
import com.bcafinance.backend_saku.features.master.role.dto.RoleResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final PermissionRepository permissionRepository;
    private final MenuRepository menuRepository;

    @Transactional(readOnly = true)
    public List<RoleResponse> findAll() {
        List<Role> roles = roleRepository.findAllByOrderByCreatedDateDesc();
        return roles.stream()
                .map(r -> {
                    List<RolePermission> rps = rolePermissionRepository.findAllByMstRoleId(r.getId());
                    return RoleResponse.builder()
                            .id(r.getId())
                            .nama(r.getNama())
                            .status(r.getStatus())
                            .totalPermissions(rps.size())
                            .createdDate(r.getCreatedDate())
                            .updatedDate(r.getUpdatedDate())
                            .build();
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public RoleDetailResponse findById(UUID id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new BussinessRuleException("Data role tidak ditemukan"));

        List<PermissionResponse> permissionList = getPermissionsByRoleId(role.getId());

        return RoleDetailResponse.builder()
                .id(role.getId())
                .nama(role.getNama())
                .status(role.getStatus())
                .createdDate(role.getCreatedDate())
                .updatedDate(role.getUpdatedDate())
                .permissions(permissionList)
                .build();
    }

    @Transactional
    public RoleDetailResponse create(RoleRequest request) {
        String roleName = request.getNama().trim().toUpperCase();

        if (roleRepository.existsByNamaIgnoreCase(roleName)) {
            throw new BussinessRuleException("Nama role '" + roleName + "' sudah digunakan");
        }

        Role role = new Role();
        role.setId(UUID.randomUUID());
        role.setNama(roleName);
        role.setStatus(request.getStatus() != null ? request.getStatus() : true);
        role.setCreatedDate(LocalDateTime.now());
        role.setUpdatedDate(LocalDateTime.now());
        Role savedRole = roleRepository.save(role);

        if (request.getPermissionIds() != null && !request.getPermissionIds().isEmpty()) {
            syncRolePermissions(savedRole.getId(), request.getPermissionIds());
        }

        return findById(savedRole.getId());
    }

    @Transactional
    public RoleDetailResponse update(UUID id, RoleRequest request) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new BussinessRuleException("Data role tidak ditemukan"));

        String roleName = request.getNama().trim().toUpperCase();
        if (!role.getNama().equalsIgnoreCase(roleName) && roleRepository.existsByNamaIgnoreCase(roleName)) {
            throw new BussinessRuleException("Nama role '" + roleName + "' sudah digunakan oleh role lain");
        }

        role.setNama(roleName);
        if (request.getStatus() != null) {
            role.setStatus(request.getStatus());
        }
        role.setUpdatedDate(LocalDateTime.now());
        roleRepository.save(role);

        if (request.getPermissionIds() != null) {
            syncRolePermissions(role.getId(), request.getPermissionIds());
        }

        return findById(role.getId());
    }

    @Transactional
    public RoleDetailResponse assignPermissions(UUID roleId, List<UUID> permissionIds) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new BussinessRuleException("Data role tidak ditemukan"));

        syncRolePermissions(role.getId(), permissionIds != null ? permissionIds : List.of());
        return findById(role.getId());
    }

    @Transactional
    public void delete(UUID id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new BussinessRuleException("Data role tidak ditemukan"));

        rolePermissionRepository.deleteAllByMstRoleId(role.getId());
        roleRepository.delete(role);
    }

    private void syncRolePermissions(UUID roleId, List<UUID> permissionIds) {
        rolePermissionRepository.deleteAllByMstRoleId(roleId);
        rolePermissionRepository.flush();

        if (permissionIds == null || permissionIds.isEmpty()) {
            return;
        }

        // Deduplicate permission IDs to prevent duplicate (mst_permission_id, mst_role_id) constraint violation
        List<UUID> uniquePermissionIds = permissionIds.stream().distinct().toList();

        List<RolePermission> listToSave = new ArrayList<>();
        for (UUID permId : uniquePermissionIds) {
            if (permissionRepository.existsById(permId)) {
                RolePermission rp = new RolePermission();
                rp.setId(UUID.randomUUID());
                rp.setMstRoleId(roleId);
                rp.setMstPermissionId(permId);
                listToSave.add(rp);
            }
        }

        if (!listToSave.isEmpty()) {
            rolePermissionRepository.saveAllAndFlush(listToSave);
        }
    }


    private List<PermissionResponse> getPermissionsByRoleId(UUID roleId) {
        List<RolePermission> rolePermissions = rolePermissionRepository.findAllByMstRoleId(roleId);
        return rolePermissions.stream()
                .map(rp -> permissionRepository.findById(rp.getMstPermissionId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(this::toPermissionResponse)
                .toList();
    }

    private PermissionResponse toPermissionResponse(Permission p) {
        String menuNama = null;
        String menuPath = null;

        if (p.getMstMenuId() != null) {
            Optional<Menu> mOpt = menuRepository.findById(p.getMstMenuId());
            if (mOpt.isPresent()) {
                menuNama = mOpt.get().getNama();
                menuPath = mOpt.get().getPath();
            }
        }

        return PermissionResponse.builder()
                .id(p.getId())
                .nama(p.getNama())
                .resource(p.getResource())
                .action(p.getAction())
                .mstMenuId(p.getMstMenuId())
                .menuNama(menuNama)
                .menuPath(menuPath)
                .createdDate(p.getCreatedDate())
                .updatedDate(p.getUpdatedDate())
                .build();
    }
}
