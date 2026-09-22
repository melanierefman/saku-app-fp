package com.bcafinance.backend_saku.core.dto;

import java.math.BigDecimal;
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
public class CustomerPinjamanHistoryItemResponse {
    private UUID pengajuanId;
    private String nomorPengajuan;
    private LocalDateTime tanggalPengajuan;
    private BigDecimal jumlahPinjaman;
    private Integer tenorBulan;
    private String statusPengajuan;
    private String tujuanPinjaman;
    private String namaCabang;
}
