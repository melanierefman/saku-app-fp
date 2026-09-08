package com.bcafinance.backend_saku.features.backoffice.dto;

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
public class PencairanItemResponse {

    private UUID pengajuanId;
    private String noPengajuan;
    private UUID customerId;
    private String namaCustomer;
    private String nik;
    private String email;
    private String noHp;
    private String namaBank;
    private String noRekening;
    private String namaRekening;
    private BigDecimal jumlahPinjaman;
    private BigDecimal biayaAdmin;
    private BigDecimal jumlahPencairan;
    private Integer tenorBulan;
    private BigDecimal bunga;
    private String namaCabang;
    private String statusPengajuan;
    private LocalDateTime tanggalDisetujuiBM;
    private String statusPencairan;
    private LocalDateTime tanggalPencairan;
    private String namaPetugasBackoffice;
}
