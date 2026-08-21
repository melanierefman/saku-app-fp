package com.bcafinance.backend_saku.core.repository;

import com.bcafinance.backend_saku.core.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
}
