package com.bcafinance.backend_saku.features.superadmin.monitoring.dto;

import java.time.LocalDateTime;
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
public class RoleStatusResponse {
    private String status;
    private LocalDateTime tanggal;
}
