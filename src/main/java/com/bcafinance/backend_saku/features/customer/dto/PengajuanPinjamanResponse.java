package com.bcafinance.backend_saku.features.customer.dto;

import com.bcafinance.backend_saku.core.dto.AngsuranItemResponse;
import com.bcafinance.backend_saku.core.dto.DokumenPinjamanResponse;
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
public class PengajuanPinjamanResponse {

    private UUID id;
    private String nomorPengajuan;
    private UUID customerId;
    private String namaCustomer;
    private UUID branchId;
    private String namaCabang;
    private String kotaCabang;
    private BigDecimal jumlahPinjaman;
    private Integer tenorBulan;
    private String tujuanPinjaman;
    private BigDecimal bunga;
    private BigDecimal biayaAdmin;
    private BigDecimal estimasiAngsuranBulanan;
    private Integer skorKesehatan;
    private String statusPengajuan;
    private String catatanReview;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private List<DokumenPinjamanResponse> dokumenList;
    private List<AngsuranItemResponse> listAngsuran;
}


