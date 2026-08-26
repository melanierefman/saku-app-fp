package com.bcafinance.backend_saku.core.repository;

import com.bcafinance.backend_saku.core.entity.AuditLog;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID>, JpaSpecificationExecutor<AuditLog> {

    Page<AuditLog> findAllByOrderByCreatedDateDesc(Pageable pageable);
}


