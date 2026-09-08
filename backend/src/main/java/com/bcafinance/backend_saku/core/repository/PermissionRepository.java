package com.bcafinance.backend_saku.core.repository;

import com.bcafinance.backend_saku.core.entity.Permission;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission, UUID> {

    List<Permission> findAllByOrderByCreatedDateDesc();

    List<Permission> findAllByMstMenuIdOrderByNamaAsc(UUID mstMenuId);

    List<Permission> findAllByResourceIgnoreCaseOrderByActionAsc(String resource);

    Optional<Permission> findByNamaIgnoreCaseAndMstMenuId(String nama, UUID mstMenuId);

    boolean existsByNamaIgnoreCaseAndMstMenuId(String nama, UUID mstMenuId);

    Optional<Permission> findByResourceIgnoreCaseAndActionIgnoreCase(String resource, String action);

}

