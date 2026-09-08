package com.bcafinance.backend_saku.features.backoffice.dto;

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
public class PencairanResponse {

    private UUID pencairanId;
    private UUID pengajuanId;
    private String nomorPengajuan;
    private UUID customerId;
    private String namaCustomer;
    private String namaBank;
    private String noRekening;
    private String namaRekening;
    private BigDecimal jumlahPinjaman;
    private BigDecimal biayaAdmin;
    private BigDecimal jumlahPencairan;
    private String statusPencairan;
    private String statusPengajuan;
    private LocalDateTime tanggalPencairan;
    private UUID disbursedByKaryawanId;
    private String namaPetugasBackoffice;
    private List<AngsuranItemResponse> listAngsuran;
}
