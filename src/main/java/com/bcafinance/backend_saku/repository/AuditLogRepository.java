package com.bcafinance.backend_saku.repository;

import com.bcafinance.backend_saku.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
}
