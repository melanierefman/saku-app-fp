package com.bcafinance.backend_saku.features.customer.dto;

import com.bcafinance.backend_saku.core.dto.AlamatDetailResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "ID Customer", example = "cu123e45-6789-4bc5-a123-456789abcdef")
    private UUID id;

    @Schema(description = "Nomor Induk Kependudukan (16 digit)", example = "3171234567890001")
    private String nik;

    @Schema(description = "Nama Lengkap sesuai KTP", example = "Budi Santoso")
    private String nama;

    @Schema(description = "Username login", example = "budi_santoso")
    private String username;

    @Schema(description = "Alamat Email", example = "budi.santoso@example.com")
    private String email;

    @Schema(description = "Nomor Handphone", example = "081234567890")
    private String noHp;

    @Schema(description = "Nama Gadis Ibu Kandung", example = "Siti Rahma")
    private String namaIbuKandung;

    // Rekening Pencairan
    @Schema(description = "Nama Bank Pencairan", example = "BCA")
    private String namaBank;

    @Schema(description = "Nomor Rekening Bank", example = "8830123456")
    private String noRekening;

    @Schema(description = "Nama Pemilik Rekening", example = "BUDI SANTOSO")
    private String namaRekening;

    // Status Akun & KYC
    @Schema(description = "Status Keaktifan Akun", example = "true")
    private Boolean status;

    @Schema(description = "Status Verifikasi KYC", example = "true")
    private Boolean isKycVerified;

    @Schema(description = "Status Tahap Verifikasi", example = "VERIFIED")
    private String statusVerifikasi;

    @Schema(description = "Catatan Tim Verifikator", example = "Dokumen KTP dan foto selfie telah sesuai")
    private String catatanVerifikasi;

    @Schema(description = "Waktu Verifikasi Selesai", example = "2026-09-22T10:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime tanggalVerifikasi;

    // Alamat
    @Schema(description = "Detail Alamat sesuai KTP")
    private AlamatDetailResponse alamatKtp;

    @Schema(description = "Detail Alamat Domisili saat ini")
    private AlamatDetailResponse alamatDomisili;

    // Foto Identitas
    @Schema(description = "URL path foto KTP", example = "/uploads/ktp/ktp_budi.jpg")
    private String fotoKtp;

    @Schema(description = "URL path foto selfie pemegang KTP", example = "/uploads/selfie/selfie_budi.jpg")
    private String fotoSelfie;

    // Pekerjaan & Finansial
    @Schema(description = "Jenis Pekerjaan", example = "Karyawan Swasta")
    private String pekerjaan;

    @Schema(description = "Nama Perusahaan / Tempat Kerja", example = "PT Teknologi Maju Bersama")
    private String tempatKerja;

    @Schema(description = "Status Pekerjaan", example = "KARYAWAN_TETAP")
    private String statusPekerjaan;

    @Schema(description = "Penghasilan Bulanan (Rp)", example = "10000000")
    private BigDecimal penghasilanBulanan;

    @Schema(description = "Lama Bekerja (Bulan)", example = "36")
    private Integer lamaBekerjaBulan;

    @Schema(description = "Total Cicilan di Tempat Lain Bulanan (Rp)", example = "1500000")
    private BigDecimal totalCicilanLainBulanan;

    @Schema(description = "Skor Kredit Internal", example = "780")
    private Integer skorKredit;

    @Schema(description = "Status Hasil Scoring Kredit", example = "LOW_RISK")
    private String statusScoring;

    // Limit & Plafond Pinjaman
    @Schema(description = "Total Limit Plafond Pinjaman (Rp)", example = "25000000")
    private BigDecimal totalPlafond;

    @Schema(description = "Total Limit Plafond Terpakai (Rp)", example = "10000000")
    private BigDecimal usedPlafond;

    @Schema(description = "Sisa Limit Plafond Tersedia (Rp)", example = "15000000")
    private BigDecimal availablePlafond;

    @Schema(description = "Tier Plafond Nasabah", example = "Plafond Gold")
    private String tierPlafond;

    @Schema(description = "Suku Bunga Bulanan (%)", example = "1.25")
    private BigDecimal sukuBunga;

    @Schema(description = "Biaya Administrasi Pinjaman (Rp)", example = "50000")
    private BigDecimal biayaAdmin;

    @Schema(description = "Tanggal Registrasi Akun", example = "2026-09-01T08:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdDate;
}
