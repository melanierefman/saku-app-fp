package com.bcafinance.backend_saku.core.repository;

import com.bcafinance.backend_saku.core.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PermissionRepository extends JpaRepository<Permission, UUID> {
}
