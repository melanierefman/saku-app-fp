package com.bcafinance.backend_saku.repository;

import com.bcafinance.backend_saku.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PermissionRepository extends JpaRepository<Permission, UUID> {
}
