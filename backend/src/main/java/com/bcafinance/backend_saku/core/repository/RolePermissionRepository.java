package com.bcafinance.backend_saku.core.repository;

import com.bcafinance.backend_saku.core.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface RolePermissionRepository extends JpaRepository<RolePermission, UUID> {

    List<RolePermission> findAllByMstRoleId(UUID roleId);

    @Modifying
    @Transactional
    @Query("DELETE FROM RolePermission rp WHERE rp.mstRoleId = :roleId")
    void deleteAllByMstRoleId(@Param("roleId") UUID roleId);

    boolean existsByMstRoleIdAndMstPermissionId(UUID roleId, UUID permissionId);
}


