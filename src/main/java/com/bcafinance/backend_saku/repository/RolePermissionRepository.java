package com.bcafinance.backend_saku.repository;

import com.bcafinance.backend_saku.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface RolePermissionRepository extends JpaRepository<RolePermission, UUID> {
}
