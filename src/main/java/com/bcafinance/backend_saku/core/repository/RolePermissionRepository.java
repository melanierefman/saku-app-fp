package com.bcafinance.backend_saku.core.repository;

import com.bcafinance.backend_saku.core.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface RolePermissionRepository extends JpaRepository<RolePermission, UUID> {

    List<RolePermission> findAllByMstRoleId(UUID roleId);
}
