package com.bcafinance.backend_saku.features.backoffice.dto;

import com.bcafinance.backend_saku.core.dto.AlamatDetailResponse;
import com.bcafinance.backend_saku.core.dto.DokumenPinjamanResponse;
import com.bcafinance.backend_saku.features.marketing.dto.ReviewPengajuanResponse;
import com.bcafinance.backend_saku.features.branchmanager.dto.PersetujuanPinjamanResponse;
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
public class PencairanDetailResponse {

    // 1. Info Pengajuan
    private UUID pengajuanId;
    private String nomorPengajuan;
    private LocalDateTime tanggalPengajuan;
    private String statusPengajuan;
    private String catatanPengajuan;

    // 2. Data Customer
    private UUID customerId;
    private String namaLengkap;
    private String nik;
    private String email;
    private String noHp;
    private AlamatDetailResponse alamatKtp;
    private AlamatDetailResponse alamatDomisili;

    // 3. Info Rekening Pencairan
    private String namaBank;
    private String noRekening;
    private String namaRekening;

    // 4. Rincian Pinjaman & Keuangan
    private BigDecimal jumlahPinjaman;
    private Integer tenorBulan;
    private String tujuanPinjaman;
    private BigDecimal bunga;
    private BigDecimal biayaAdmin;
    private BigDecimal jumlahPencairanBersih;
    private BigDecimal estimasiAngsuranBulanan;
    private UUID branchId;
    private String namaCabang;
    private String kotaCabang;

    // 5. Dokumen Foto Identitas & Pinjaman
    private String fotoSelfie;
    private String fotoKtp;
    private List<DokumenPinjamanResponse> dokumenPinjamanList;

    // 6. Riwayat Review & Approval
    private ReviewPengajuanResponse reviewMarketingTerakhir;
    private PersetujuanPinjamanResponse persetujuanBMTerakhir;

    // 7. Status Pencairan & Jadwal Angsuran
    private UUID pencairanId;
    private String statusPencairan;
    private LocalDateTime tanggalPencairan;
    private UUID disbursedByKaryawanId;
    private String namaPetugasBackoffice;
    private List<AngsuranItemResponse> listAngsuran;
}
