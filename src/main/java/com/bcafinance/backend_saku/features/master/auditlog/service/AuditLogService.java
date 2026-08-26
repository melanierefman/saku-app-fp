package com.bcafinance.backend_saku.features.master.auditlog.service;

import com.bcafinance.backend_saku.core.entity.AuditLog;
import com.bcafinance.backend_saku.core.entity.Karyawan;
import com.bcafinance.backend_saku.core.repository.AuditLogRepository;
import com.bcafinance.backend_saku.core.repository.KaryawanRepository;
import com.bcafinance.backend_saku.features.master.auditlog.dto.AuditLogItemResponse;
import com.bcafinance.backend_saku.features.master.auditlog.dto.AuditLogPageResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final KaryawanRepository karyawanRepository;

    @Transactional(readOnly = true)
    public AuditLogPageResponse getAuditLogs(
            String action,
            String entity,
            UUID karyawanId,
            String keyword,
            int page,
            int size) {

        String act = (action != null && !action.isBlank()) ? action.trim().toLowerCase() : null;
        String ent = (entity != null && !entity.isBlank()) ? entity.trim().toLowerCase() : null;
        String kw = (keyword != null && !keyword.isBlank()) ? keyword.trim().toLowerCase() : null;

        org.springframework.data.jpa.domain.Specification<AuditLog> spec = (root, query, cb) -> {
            java.util.List<jakarta.persistence.criteria.Predicate> predicates = new java.util.ArrayList<>();

            if (act != null) {
                predicates.add(cb.equal(cb.lower(root.get("action")), act));
            }
            if (ent != null) {
                predicates.add(cb.equal(cb.lower(root.get("entity")), ent));
            }
            if (karyawanId != null) {
                predicates.add(cb.equal(root.get("mstKaryawanId"), karyawanId));
            }
            if (kw != null) {
                String pattern = "%" + kw + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("description")), pattern),
                        cb.like(cb.lower(root.get("action")), pattern),
                        cb.like(cb.lower(root.get("entity")), pattern)
                ));
            }

            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };

        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size), Sort.by(Sort.Direction.DESC, "createdDate"));
        Page<AuditLog> logPage = auditLogRepository.findAll(spec, pageable);

        List<AuditLogItemResponse> items = logPage.getContent().stream()
                .map(this::mapToItemResponse)
                .toList();

        return AuditLogPageResponse.builder()
                .totalItems(logPage.getTotalElements())
                .totalPages(logPage.getTotalPages())
                .currentPage(logPage.getNumber())
                .pageSize(logPage.getSize())
                .logs(items)
                .build();
    }


    @Transactional
    public void recordLog(UUID karyawanId, String action, String entity, Integer entityId, String description) {
        if (karyawanId == null) {
            return;
        }
        try {
            AuditLog logEntry = new AuditLog();
            logEntry.setId(UUID.randomUUID());
            logEntry.setMstKaryawanId(karyawanId);
            logEntry.setAction(action != null ? truncate(action.toUpperCase(), 20) : "ACTIVITY");
            logEntry.setEntity(entity != null ? truncate(entity.toUpperCase(), 20) : "GENERAL");
            logEntry.setEntityId(entityId != null ? entityId : 0);
            logEntry.setDescription(description != null ? truncate(description, 225) : "");
            logEntry.setCreatedDate(LocalDateTime.now());

            auditLogRepository.save(logEntry);
            log.info("Audit log recorded: [{}] {} by karyawan {}", action, description, karyawanId);
        } catch (Exception e) {
            log.error("Failed to record audit log: {}", e.getMessage(), e);
        }
    }

    @Transactional
    public void recordLog(UUID karyawanId, String action, String entity, String description) {
        recordLog(karyawanId, action, entity, 0, description);
    }

    private String truncate(String val, int maxLen) {
        if (val == null) return "";
        return val.length() > maxLen ? val.substring(0, maxLen) : val;
    }


    private AuditLogItemResponse mapToItemResponse(AuditLog a) {
        AuditLogItemResponse.KaryawanAuditInfo karyawanInfo = null;

        if (a.getMstKaryawanId() != null) {
            Optional<Karyawan> karyawanOpt = karyawanRepository.findById(a.getMstKaryawanId());
            if (karyawanOpt.isPresent()) {
                Karyawan k = karyawanOpt.get();
                String roleName = k.getRole() != null ? k.getRole().getNama() : "-";
                String cabangName = k.getCabang() != null ? k.getCabang().getNama() : "-";

                karyawanInfo = AuditLogItemResponse.KaryawanAuditInfo.builder()
                        .id(k.getId())
                        .nama(k.getNama())
                        .email(k.getEmail())
                        .username(k.getUsername())
                        .role(roleName)
                        .cabang(cabangName)
                        .build();
            }
        }

        return AuditLogItemResponse.builder()
                .id(a.getId())
                .action(a.getAction())
                .entity(a.getEntity())
                .entityId(a.getEntityId())
                .description(a.getDescription())
                .createdDate(a.getCreatedDate())
                .karyawan(karyawanInfo)
                .build();
    }
}
