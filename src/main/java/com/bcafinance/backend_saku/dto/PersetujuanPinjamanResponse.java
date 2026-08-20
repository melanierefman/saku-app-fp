package com.bcafinance.backend_saku.dto;

import java.time.LocalDateTime;
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
public class PersetujuanPinjamanResponse {

    private UUID id;
    private UUID pengajuanId;
    private String nomorPengajuan;
    private String hasilPersetujuan;
    private String catatan;
    private UUID approverId;
    private String namaApprover;
    private LocalDateTime tanggalPersetujuan;
    private String statusPengajuan;
}
