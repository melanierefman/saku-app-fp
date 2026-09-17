package com.bcafinance.backend_saku.features.backoffice.dto;

import com.bcafinance.backend_saku.core.dto.AlamatDetailResponse;
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
public class VerifikasiCustomerDetailResponse {

    // Data Identitas Customer
    private UUID customerId;
    private String namaLengkap;
    private String nik;
    private String email;
    private String noHp;
    private String username;
    private String namaIbuKandung;

    // Pekerjaan & Keuangan (Informasi Dasar)
    private String pekerjaan;
    private String tempatKerja;
    private String statusPekerjaan;
    private BigDecimal pendapatan;
    private BigDecimal penghasilanBulanan;

    // Rekening
    private String namaBank;
    private String noRekening;
    private String namaRekening;

    // Alamat
    private AlamatDetailResponse alamatKtp;
    private AlamatDetailResponse alamatDomisili;

    // Dokumen Foto
    private String fotoSelfie;
    private String fotoKtp;

    // Status Verifikasi
    private String statusVerifikasi;
    private String catatanVerifikasi;
    private LocalDateTime tanggalPengajuan;
    private LocalDateTime tanggalVerifikasi;
    private UUID verifiedByKaryawanId;
}
