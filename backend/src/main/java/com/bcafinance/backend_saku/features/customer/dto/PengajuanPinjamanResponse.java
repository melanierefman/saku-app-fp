package com.bcafinance.backend_saku.features.customer.dto;

import com.bcafinance.backend_saku.core.dto.AngsuranItemResponse;
import com.bcafinance.backend_saku.core.dto.DokumenPinjamanResponse;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "ID Pengajuan Pinjaman", example = "pj123e45-6789-4bc5-a123-456789abcdef")
    private UUID id;

    @Schema(description = "Nomor Registrasi Pengajuan", example = "PJ-202609-0012")
    private String nomorPengajuan;

    @Schema(description = "ID Customer Pemohon", example = "cu123e45-6789-4bc5-a123-456789abcdef")
    private UUID customerId;

    @Schema(description = "Nama Lengkap Nasabah", example = "Budi Santoso")
    private String namaCustomer;

    @Schema(description = "ID Cabang Pemroses", example = "cb123e45-6789-4bc5-a123-456789abcdef")
    private UUID branchId;

    @Schema(description = "Nama Cabang", example = "Cabang Jakarta Pusat")
    private String namaCabang;

    @Schema(description = "Kota Cabang", example = "Jakarta Pusat")
    private String kotaCabang;

    @Schema(description = "Jumlah Pinjaman Diajukan (Rp)", example = "15000000")
    private BigDecimal jumlahPinjaman;

    @Schema(description = "Tenor Pinjaman (Bulan)", example = "12")
    private Integer tenorBulan;

    @Schema(description = "Tujuan Penggunaan Pinjaman", example = "Renovasi Rumah & Modal Usaha")
    private String tujuanPinjaman;

    @Schema(description = "Suku Bunga Bulanan (%)", example = "1.25")
    private BigDecimal bunga;

    @Schema(description = "Biaya Admin Pinjaman (Rp)", example = "50000")
    private BigDecimal biayaAdmin;

    @Schema(description = "Estimasi Cicilan per Bulan (Rp)", example = "1437500")
    private BigDecimal estimasiAngsuranBulanan;

    @Schema(description = "Skor Analisis Kredit / Kesehatan Finansial", example = "85")
    private Integer skorKesehatan;

    @Schema(description = "Status Pengajuan", example = "DISETUJUI")
    private String statusPengajuan;

    @Schema(description = "Catatan Hasil Review / Persetujuan", example = "Kapasitas finansial dan dokumen lengkap dan valid")
    private String catatanReview;

    @Schema(description = "Waktu Pengajuan Dibuat", example = "2026-09-22T08:30:00")
    private LocalDateTime createdDate;

    @Schema(description = "Waktu Terakhir Diperbarui", example = "2026-09-22T09:45:00")
    private LocalDateTime updatedDate;

    @Schema(description = "Daftar Berkas & Dokumen Terlampir")
    private List<DokumenPinjamanResponse> dokumenList;

    @Schema(description = "Jadwal Simulasi / Rencana Angsuran")
    private List<AngsuranItemResponse> listAngsuran;
}


