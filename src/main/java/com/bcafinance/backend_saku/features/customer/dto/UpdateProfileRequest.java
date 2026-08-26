package com.bcafinance.backend_saku.features.customer.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {

    // Nomor HP
    private String noHp;

    // Rekening
    private String namaBank;
    private String noRekening;
    private String namaRekening;

    // Domisili
    private String domisiliAlamatLengkap;
    private String domisiliRt;
    private String domisiliRw;
    private String domisiliKelurahan;
    private String domisiliKecamatan;
    private String domisiliKotaKabupaten;
    private String domisiliProvinsi;
    private String domisiliKodePos;

    // Pekerjaan & Finansial
    private String pekerjaan;
    private String tempatKerja;
    private String statusPekerjaan;
    private BigDecimal penghasilanBulanan;
    private Integer lamaBekerjaBulan;
    private BigDecimal totalCicilanLainBulanan;
}
