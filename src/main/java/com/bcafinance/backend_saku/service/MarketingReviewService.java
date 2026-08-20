package com.bcafinance.backend_saku.service;

import com.bcafinance.backend_saku.dto.AlamatDetailResponse;
import com.bcafinance.backend_saku.dto.DokumenPinjamanResponse;
import com.bcafinance.backend_saku.dto.MarketingPengajuanDetailResponse;
import com.bcafinance.backend_saku.dto.MarketingPengajuanItemResponse;
import com.bcafinance.backend_saku.dto.ReviewPengajuanRequest;
import com.bcafinance.backend_saku.dto.ReviewPengajuanResponse;
import com.bcafinance.backend_saku.dto.ScoringAnalysisResponse;
import com.bcafinance.backend_saku.entity.AlamatCustomer;
import com.bcafinance.backend_saku.entity.Cabang;
import com.bcafinance.backend_saku.entity.Customer;
import com.bcafinance.backend_saku.entity.DokumenCustomer;
import com.bcafinance.backend_saku.entity.DokumenPinjaman;
import com.bcafinance.backend_saku.entity.Karyawan;
import com.bcafinance.backend_saku.entity.PengajuanPinjaman;
import com.bcafinance.backend_saku.entity.ReviewPengajuan;
import com.bcafinance.backend_saku.entity.ScoringCustomer;
import com.bcafinance.backend_saku.exception.BussinessRuleException;
import com.bcafinance.backend_saku.repository.AlamatCustomerRepository;
import com.bcafinance.backend_saku.repository.CabangRepository;
import com.bcafinance.backend_saku.repository.CustomerRepository;
import com.bcafinance.backend_saku.repository.DokumenCustomerRepository;
import com.bcafinance.backend_saku.repository.DokumenPinjamanRepository;
import com.bcafinance.backend_saku.repository.KaryawanRepository;
import com.bcafinance.backend_saku.repository.PengajuanPinjamanRepository;
import com.bcafinance.backend_saku.repository.ReviewPengajuanRepository;
import com.bcafinance.backend_saku.repository.ScoringCustomerRepository;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MarketingReviewService {

    private final PengajuanPinjamanRepository pengajuanRepository;
    private final ReviewPengajuanRepository reviewPengajuanRepository;
    private final CustomerRepository customerRepository;
    private final AlamatCustomerRepository alamatRepository;
    private final DokumenCustomerRepository dokumenCustomerRepository;
    private final DokumenPinjamanRepository dokumenPinjamanRepository;
    private final ScoringCustomerRepository scoringRepository;
    private final KaryawanRepository karyawanRepository;
    private final CabangRepository cabangRepository;
    private final ScoringService scoringService;

    public List<MarketingPengajuanItemResponse> findAll(String statusFilter) {
        List<PengajuanPinjaman> list = pengajuanRepository.findAllByOrderByCreatedDateDesc();

        return list.stream()
                .map(p -> {
                    Customer customer = customerRepository.findById(p.getMstCustomerId()).orElse(null);
                    Cabang cabang = cabangRepository.findById(p.getMstBranchId()).orElse(null);
                    Optional<ReviewPengajuan> latestReviewOpt = reviewPengajuanRepository
                            .findFirstByTrxPengajuanPinjamanIdOrderByCreatedDateDesc(p.getId());

                    String statusTampilan = mapStatusTampilan(p.getStatusPengajuan());

                    boolean isRevisiSubmitted = "PENDING".equalsIgnoreCase(p.getStatusPengajuan())
                            && latestReviewOpt.isPresent()
                            && "PERLU_REVISI".equalsIgnoreCase(latestReviewOpt.get().getHasilReview());

                    String hasilReviewTerakhir;
                    String catatanTerakhir;
                    LocalDateTime tglReviewTerakhir;

                    if (isRevisiSubmitted) {
                        hasilReviewTerakhir = "DOKUMEN_DIREVISI";
                        catatanTerakhir = p.getCatatanReview();
                        tglReviewTerakhir = p.getUpdatedDate();
                    } else {
                        hasilReviewTerakhir = latestReviewOpt.map(ReviewPengajuan::getHasilReview).orElse(null);
                        catatanTerakhir = latestReviewOpt.map(ReviewPengajuan::getCatatan).orElse(p.getCatatanReview());
                        tglReviewTerakhir = latestReviewOpt.map(ReviewPengajuan::getCreatedDate).orElse(null);
                    }

                    return MarketingPengajuanItemResponse.builder()
                            .pengajuanId(p.getId())
                            .noPengajuan(p.getNomorPengajuan())
                            .customerId(p.getMstCustomerId())
                            .customer(customer != null ? customer.getNama() : "-")
                            .email(customer != null ? customer.getEmail() : "-")
                            .noHp(customer != null ? customer.getNoHp() : "-")
                            .tanggalPengajuan(p.getCreatedDate())
                            .jumlah(p.getJumlahPinjaman())
                            .tenor(p.getTenorBulan())
                            .cabang(cabang != null ? cabang.getNama() : "-")
                            .status(statusTampilan)
                            .hasilReviewTerakhir(hasilReviewTerakhir)
                            .catatanReviewTerakhir(catatanTerakhir)
                            .tanggalReviewTerakhir(tglReviewTerakhir)
                            .build();
                })
                .filter(item -> {
                    if (statusFilter == null || statusFilter.isBlank() || "ALL".equalsIgnoreCase(statusFilter)) {
                        return true;
                    }
                    if ("MENUNGGU_REVIEW".equalsIgnoreCase(statusFilter) || "PENDING".equalsIgnoreCase(statusFilter)) {
                        return "MENUNGGU_REVIEW".equalsIgnoreCase(item.getStatus()) || "PENDING".equalsIgnoreCase(item.getStatus());
                    }
                    if ("SELESAI_DIREVIEW".equalsIgnoreCase(statusFilter) || "DISETUJUI".equalsIgnoreCase(statusFilter)) {
                        return "SELESAI_DIREVIEW".equalsIgnoreCase(item.getStatus());
                    }
                    if ("PENGAJUAN_DITOLAK".equalsIgnoreCase(statusFilter) || "DITOLAK".equalsIgnoreCase(statusFilter)) {
                        return "PENGAJUAN_DITOLAK".equalsIgnoreCase(item.getStatus());
                    }
                    if ("PERLU_REVISI".equalsIgnoreCase(statusFilter)) {
                        return "PERLU_REVISI".equalsIgnoreCase(item.getStatus());
                    }
                    return statusFilter.equalsIgnoreCase(item.getStatus());
                })
                .toList();
    }


    public MarketingPengajuanDetailResponse getDetail(UUID pengajuanId) {
        PengajuanPinjaman pengajuan = pengajuanRepository.findById(pengajuanId)
                .orElseThrow(() -> new BussinessRuleException("Pengajuan pinjaman tidak ditemukan"));

        UUID customerId = pengajuan.getMstCustomerId();
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new BussinessRuleException("Data nasabah tidak ditemukan"));

        Cabang cabang = cabangRepository.findById(pengajuan.getMstBranchId()).orElse(null);

        // Alamat Nasabah
        Optional<AlamatCustomer> alamatKtpOpt = alamatRepository.findByCustomer_IdAndJenisAlamat(customerId, "KTP");
        Optional<AlamatCustomer> alamatDomisiliOpt = alamatRepository.findByCustomer_IdAndJenisAlamat(customerId, "DOMISILI");

        // Dokumen Identitas (KTP & Selfie)
        Optional<DokumenCustomer> ktpDocOpt = dokumenCustomerRepository.findByCustomer_IdAndDocType(customerId, "KTP");
        Optional<DokumenCustomer> selfieDocOpt = dokumenCustomerRepository.findByCustomer_IdAndDocType(customerId, "SELFIE");

        // Dokumen Pinjaman (Slip Gaji, Rekening Koran, NPWP)
        List<DokumenPinjaman> loanDocs = dokumenPinjamanRepository.findAllByTrxPengajuanPinjamanId(pengajuanId);
        String slipGajiUrl = loanDocs.stream()
                .filter(d -> "SLIP_GAJI".equalsIgnoreCase(d.getDocType()))
                .map(DokumenPinjaman::getFileUrl)
                .findFirst().orElse(null);
        String rekeningKoranUrl = loanDocs.stream()
                .filter(d -> "REKENING_KORAN".equalsIgnoreCase(d.getDocType()))
                .map(DokumenPinjaman::getFileUrl)
                .findFirst().orElse(null);
        String npwpUrl = loanDocs.stream()
                .filter(d -> "NPWP".equalsIgnoreCase(d.getDocType()))
                .map(DokumenPinjaman::getFileUrl)
                .findFirst().orElse(null);

        List<DokumenPinjamanResponse> docListResponse = loanDocs.stream()
                .map(d -> DokumenPinjamanResponse.builder()
                        .id(d.getId())
                        .docType(d.getDocType())
                        .fileUrl(d.getFileUrl())
                        .createdDate(d.getCreatedDate())
                        .build())
                .toList();

        // Data Scoring Customer & Analisis Ambigu
        Optional<ScoringCustomer> scoringOpt = Optional.empty();
        if (pengajuan.getTrxScoringCustomerId() != null) {
            scoringOpt = scoringRepository.findById(pengajuan.getTrxScoringCustomerId());
        }
        if (scoringOpt.isEmpty()) {
            scoringOpt = scoringRepository.findFirstByMstCustomerIdOrderByCreatedDateDesc(customerId);
        }

        ScoringAnalysisResponse analysis = scoringOpt.map(scoringService::analyzeScoring).orElse(null);

        // Riwayat Review Pengajuan
        List<ReviewPengajuan> reviewEntities = reviewPengajuanRepository
                .findAllByTrxPengajuanPinjamanIdOrderByCreatedDateDesc(pengajuanId);

        List<ReviewPengajuanResponse> reviewHistory = reviewEntities.stream()
                .map(r -> {
                    String reviewerName = karyawanRepository.findById(r.getMstKaryawanId())
                            .map(Karyawan::getNama)
                            .orElse("-");
                    return ReviewPengajuanResponse.builder()
                            .id(r.getId())
                            .pengajuanId(pengajuanId)
                            .nomorPengajuan(pengajuan.getNomorPengajuan())
                            .hasilReview(r.getHasilReview())
                            .catatan(r.getCatatan())
                            .reviewerId(r.getMstKaryawanId())
                            .namaReviewer(reviewerName)
                            .tanggalReview(r.getCreatedDate())
                            .statusPengajuan(pengajuan.getStatusPengajuan())
                            .build();
                })
                .toList();

        ReviewPengajuanResponse latestReview = reviewHistory.isEmpty() ? null : reviewHistory.get(0);

        BigDecimal estimasiCicilan = calculateEstimasiAngsuran(
                pengajuan.getJumlahPinjaman(), pengajuan.getTenorBulan(), pengajuan.getBunga());

        return MarketingPengajuanDetailResponse.builder()
                // Info Pengajuan
                .pengajuanId(pengajuan.getId())
                .nomorPengajuan(pengajuan.getNomorPengajuan())
                .tanggalPengajuan(pengajuan.getCreatedDate())
                .statusPengajuan(pengajuan.getStatusPengajuan())
                .catatanPengajuan(pengajuan.getCatatanReview())

                // Data Customer
                .customerId(customer.getId())
                .namaLengkap(customer.getNama())
                .email(customer.getEmail())
                .nik(customer.getNik())
                .noHp(customer.getNoHp())
                .pekerjaan(scoringOpt.map(ScoringCustomer::getPekerjaan).orElse(null))
                .tempatKerja(scoringOpt.map(ScoringCustomer::getTempatKerja).orElse(null))
                .statusPekerjaan(scoringOpt.map(ScoringCustomer::getStatusPekerjaan).orElse(null))
                .pendapatan(scoringOpt.map(ScoringCustomer::getPenghasilanBulanan).orElse(null))
                .penghasilanBulanan(scoringOpt.map(ScoringCustomer::getPenghasilanBulanan).orElse(null))
                .namaBank(customer.getNamaBank())
                .noRekening(customer.getNoRekening())
                .namaRekening(customer.getNamaRekening())
                .alamatKtp(alamatKtpOpt.map(this::mapAlamat).orElse(null))
                .alamatDomisili(alamatDomisiliOpt.map(this::mapAlamat).orElse(null))

                // Dokumen
                .fotoKtp(ktpDocOpt.map(DokumenCustomer::getFileUrl).orElse(null))
                .fotoSelfie(selfieDocOpt.map(DokumenCustomer::getFileUrl).orElse(null))
                .slipGaji(slipGajiUrl)
                .rekeningKoran(rekeningKoranUrl)
                .npwp(npwpUrl)
                .dokumenPinjamanList(docListResponse)

                // Hasil Scoring & Indikator Ambigu
                .scoringStatusPekerjaan(scoringOpt.map(ScoringCustomer::getStatusPekerjaan).orElse(null))
                .scoringPenghasilan(scoringOpt.map(ScoringCustomer::getPenghasilanBulanan).orElse(null))
                .lamaBekerjaBulan(scoringOpt.map(ScoringCustomer::getLamaBekerjaBulan).orElse(null))
                .lamaJadiNasabahBulan(scoringOpt.map(ScoringCustomer::getLamaJadiNasabahBulan).orElse(null))
                .cicilanBerjalan(scoringOpt.map(ScoringCustomer::getTotalCicilanLainBulanan).orElse(null))
                .skor(analysis != null ? analysis.getSkor() : (scoringOpt.map(ScoringCustomer::getSkor).orElse(null)))
                .statusScoring(analysis != null ? analysis.getStatusScoring() : (scoringOpt.map(ScoringCustomer::getStatusScoring).orElse(null)))
                .keputusanSistem(analysis != null ? analysis.getKeputusanSistem() : null)
                .dbr(analysis != null ? analysis.getDbr() : null)
                .dbrPercentage(analysis != null ? analysis.getDbrPercentage() : null)
                .plafonNama(analysis != null ? analysis.getMatchedPlafondNama() : null)
                .plafonMaksimal(analysis != null ? analysis.getMatchedPlafondMaksimal() : null)
                .estimasiPlafondDisetujui(analysis != null ? analysis.getEstimasiPlafondDisetujui() : null)
                .isAmbigu(analysis != null ? analysis.getIsAmbigu() : false)
                .notesAmbigu(analysis != null ? analysis.getIndikatorAmbigu() : List.of())
                .ringkasanAnalisis(analysis != null ? analysis.getRingkasanAnalisis() : null)
                .breakdown(analysis != null ? analysis.getBreakdown() : null)

                // Detail Pinjaman
                .jumlahPinjaman(pengajuan.getJumlahPinjaman())
                .tenorBulan(pengajuan.getTenorBulan())
                .tujuanPinjaman(pengajuan.getTujuanPinjaman())
                .bunga(pengajuan.getBunga())
                .biayaAdmin(pengajuan.getBiayaAdmin())
                .estimasiCicilan(estimasiCicilan)
                .branchId(pengajuan.getMstBranchId())
                .namaCabang(cabang != null ? cabang.getNama() : "-")
                .kotaCabang(cabang != null ? cabang.getKota() : "-")

                // Review History
                .latestReview(latestReview)
                .reviewHistory(reviewHistory)
                .build();
    }

    @Transactional
    public ReviewPengajuanResponse review(UUID pengajuanId, UUID karyawanId, ReviewPengajuanRequest request) {
        PengajuanPinjaman pengajuan = pengajuanRepository.findById(pengajuanId)
                .orElseThrow(() -> new BussinessRuleException("Pengajuan pinjaman tidak ditemukan"));

        Karyawan karyawan = karyawanRepository.findById(karyawanId)
                .orElseThrow(() -> new BussinessRuleException("Karyawan reviewer tidak ditemukan"));

        String inputReview = request.getHasilReview().trim().toUpperCase();
        String hasilReview;
        String statusPengajuan;

        if (inputReview.contains("SETUJU") || inputReview.contains("APPROV")) {
            hasilReview = "DISETUJUI";
            statusPengajuan = "SELESAI_DIREVIEW";
        } else if (inputReview.contains("TOLAK") || inputReview.contains("REJECT")) {
            hasilReview = "DITOLAK";
            statusPengajuan = "PENGAJUAN_DITOLAK";
        } else if (inputReview.contains("REVISI")) {
            hasilReview = "PERLU_REVISI";
            statusPengajuan = "PERLU_REVISI";
        } else {
            hasilReview = inputReview;
            statusPengajuan = inputReview;
        }

        ReviewPengajuan review = new ReviewPengajuan();
        review.setId(UUID.randomUUID());
        review.setHasilReview(hasilReview);
        review.setCatatan(request.getCatatan());
        review.setCreatedDate(LocalDateTime.now());
        review.setUpdatedDate(LocalDateTime.now());
        review.setMstKaryawanId(karyawanId);
        review.setTrxPengajuanPinjamanId(pengajuanId);

        ReviewPengajuan savedReview = reviewPengajuanRepository.save(review);

        pengajuan.setStatusPengajuan(statusPengajuan);
        pengajuan.setCatatanReview(request.getCatatan());
        pengajuan.setUpdatedDate(LocalDateTime.now());
        pengajuanRepository.save(pengajuan);

        return ReviewPengajuanResponse.builder()
                .id(savedReview.getId())
                .pengajuanId(pengajuanId)
                .nomorPengajuan(pengajuan.getNomorPengajuan())
                .hasilReview(hasilReview)
                .catatan(request.getCatatan())
                .reviewerId(karyawanId)
                .namaReviewer(karyawan.getNama())
                .tanggalReview(savedReview.getCreatedDate())
                .statusPengajuan(statusPengajuan)
                .build();
    }

    private String mapStatusTampilan(String rawStatus) {
        if (rawStatus == null || "PENDING".equalsIgnoreCase(rawStatus)) {
            return "MENUNGGU_REVIEW";
        }
        if ("PERLU_REVISI".equalsIgnoreCase(rawStatus)) {
            return "PERLU_REVISI";
        }
        if ("SELESAI_DIREVIEW".equalsIgnoreCase(rawStatus) || "DISETUJUI".equalsIgnoreCase(rawStatus) || "APPROVED".equalsIgnoreCase(rawStatus)) {
            return "SELESAI_DIREVIEW";
        }
        if ("PENGAJUAN_DITOLAK".equalsIgnoreCase(rawStatus) || "REJECTED".equalsIgnoreCase(rawStatus) || "DITOLAK".equalsIgnoreCase(rawStatus)) {
            return "PENGAJUAN_DITOLAK";
        }
        if ("MENUNGGU_DOKUMEN".equalsIgnoreCase(rawStatus)) {
            return "MENUNGGU_DOKUMEN";
        }

        return rawStatus;
    }


    private AlamatDetailResponse mapAlamat(AlamatCustomer alamat) {
        if (alamat == null) {
            return null;
        }

        StringBuilder formatted = new StringBuilder();
        if (alamat.getAlamatLengkap() != null) {
            formatted.append(alamat.getAlamatLengkap());
        }
        if (alamat.getRt() != null || alamat.getRw() != null) {
            formatted.append(", RT ").append(alamat.getRt() != null ? alamat.getRt() : "-")
                    .append("/RW ").append(alamat.getRw() != null ? alamat.getRw() : "-");
        }
        if (alamat.getKelurahan() != null) {
            formatted.append(", Kel. ").append(alamat.getKelurahan());
        }
        if (alamat.getKecamatan() != null) {
            formatted.append(", Kec. ").append(alamat.getKecamatan());
        }
        if (alamat.getKotaKabupaten() != null) {
            formatted.append(", ").append(alamat.getKotaKabupaten());
        }
        if (alamat.getProvinsi() != null) {
            formatted.append(", ").append(alamat.getProvinsi());
        }
        if (alamat.getKodePos() != null) {
            formatted.append(" ").append(alamat.getKodePos());
        }

        return AlamatDetailResponse.builder()
                .jenisAlamat(alamat.getJenisAlamat())
                .alamatLengkap(alamat.getAlamatLengkap())
                .rt(alamat.getRt())
                .rw(alamat.getRw())
                .kelurahan(alamat.getKelurahan())
                .kecamatan(alamat.getKecamatan())
                .kotaKabupaten(alamat.getKotaKabupaten())
                .provinsi(alamat.getProvinsi())
                .kodePos(alamat.getKodePos())
                .formattedAddress(formatted.toString().trim())
                .build();
    }

    private BigDecimal calculateEstimasiAngsuran(BigDecimal jumlahPinjaman, Integer tenorBulan, BigDecimal bungaTahunan) {
        if (jumlahPinjaman == null || tenorBulan == null || tenorBulan <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal pokokBulanan = jumlahPinjaman.divide(BigDecimal.valueOf(tenorBulan), 2, RoundingMode.HALF_UP);
        BigDecimal rate = bungaTahunan != null ? bungaTahunan : BigDecimal.ZERO;
        BigDecimal bungaBulanan = jumlahPinjaman
                .multiply(rate.movePointLeft(2))
                .divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);

        return pokokBulanan.add(bungaBulanan);
    }
}
