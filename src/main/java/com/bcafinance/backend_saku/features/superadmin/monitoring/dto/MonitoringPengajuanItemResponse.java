package com.bcafinance.backend_saku.features.superadmin.monitoring.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
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
public class MonitoringPengajuanItemResponse {

    private UUID pengajuanId;
    private String noPengajuan;
    private UUID customerId;
    private String customer;
    private String email;
    private String noHp;
    private String nik;
    private LocalDateTime tanggalPengajuan;
    private BigDecimal jumlah;
    private Integer tenor;
    private String cabang;
    private String status;
    private String statusLabel;

    // Per-role status (hanya status & tanggal)
    private RoleStatusResponse marketing;
    private RoleStatusResponse branchManager;
    private RoleStatusResponse backoffice;

    // Riwayat (array of status & tanggal per role)
    private List<RiwayatRoleStatusResponse> riwayat;

    public RoleStatusResponse getBm() {
        return branchManager;
    }
}
