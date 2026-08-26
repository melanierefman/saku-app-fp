package com.bcafinance.backend_saku.features.customer.dto;

import com.bcafinance.backend_saku.core.dto.AlamatDetailResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerProfileResponse {

    // Identitas Pribadi
    private UUID id;
    private String nik;
    private String nama;
    private String username;
    private String email;
    private String noHp;

    // Rekening Pencairan
    private String namaBank;
    private String noRekening;
    private String namaRekening;

    // Status Akun & KYC
    private Boolean status;
    private Boolean isKycVerified;
    private String statusVerifikasi;
    private String catatanVerifikasi;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime tanggalVerifikasi;

    // Alamat
    private AlamatDetailResponse alamatKtp;
    private AlamatDetailResponse alamatDomisili;

    // Foto Identitas
    private String fotoKtp;
    private String fotoSelfie;

    // Pekerjaan & Finansial
    private String pekerjaan;
    private String tempatKerja;
    private String statusPekerjaan;
    private BigDecimal penghasilanBulanan;
    private Integer lamaBekerjaBulan;
    private BigDecimal totalCicilanLainBulanan;
    private Integer skorKredit;
    private String statusScoring;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdDate;
}
