package com.bcafinance.backend_saku.features.superadmin.auditlog.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLogPageResponse {

    private long totalItems;
    private int totalPages;
    private int currentPage;
    private int pageSize;
    private List<AuditLogItemResponse> logs;
}
