package com.bcafinance.backend_saku.features.marketing.service;

import com.bcafinance.backend_saku.core.dto.AlamatDetailResponse;
import com.bcafinance.backend_saku.core.dto.DokumenPinjamanResponse;
import com.bcafinance.backend_saku.features.marketing.dto.MarketingDashboardStatsResponse;
import com.bcafinance.backend_saku.features.marketing.dto.MarketingPengajuanDetailResponse;

import com.bcafinance.backend_saku.features.marketing.dto.MarketingPengajuanItemResponse;
import com.bcafinance.backend_saku.features.marketing.dto.ReviewPengajuanRequest;
import com.bcafinance.backend_saku.features.marketing.dto.ReviewPengajuanResponse;
import com.bcafinance.backend_saku.features.scoring.dto.ScoringAnalysisResponse;
import com.bcafinance.backend_saku.features.scoring.service.ScoringService;
import com.bcafinance.backend_saku.core.entity.AlamatCustomer;

import com.bcafinance.backend_saku.core.entity.Cabang;
import com.bcafinance.backend_saku.core.entity.Customer;
import com.bcafinance.backend_saku.core.entity.DokumenCustomer;
import com.bcafinance.backend_saku.core.entity.DokumenPinjaman;
import com.bcafinance.backend_saku.core.entity.Karyawan;
import com.bcafinance.backend_saku.core.entity.PengajuanPinjaman;
import com.bcafinance.backend_saku.core.entity.ReviewPengajuan;
import com.bcafinance.backend_saku.core.entity.ScoringCustomer;
import com.bcafinance.backend_saku.core.exception.BussinessRuleException;
import com.bcafinance.backend_saku.core.repository.AlamatCustomerRepository;
import com.bcafinance.backend_saku.core.repository.CabangRepository;
import com.bcafinance.backend_saku.core.repository.CustomerRepository;
import com.bcafinance.backend_saku.core.repository.DokumenCustomerRepository;
import com.bcafinance.backend_saku.core.repository.DokumenPinjamanRepository;
import com.bcafinance.backend_saku.core.repository.KaryawanRepository;
import com.bcafinance.backend_saku.core.repository.PengajuanPinjamanRepository;
import com.bcafinance.backend_saku.core.repository.ReviewPengajuanRepository;
import com.bcafinance.backend_saku.core.repository.ScoringCustomerRepository;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.bcafinance.backend_saku.core.dto.PageResponse;

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
    private final com.bcafinance.backend_saku.features.customer.service.NotifikasiService notifikasiService;
    private final com.bcafinance.backend_saku.features.superadmin.auditlog.service.AuditLogService auditLogService;

    public PageResponse<MarketingPengajuanItemResponse> findAllPaginated(int page, int size, String search, String statusFilter, UUID karyawanId) {
        List<MarketingPengajuanItemResponse> all = findAll(statusFilter, karyawanId);

        if (search != null && !search.trim().isEmpty()) {
            String s = search.trim().toLowerCase();
            all = all.stream().filter(item ->
                    (item.getNoPengajuan() != null && item.getNoPengajuan().toLowerCase().contains(s)) ||
                    (item.getCustomer() != null && item.getCustomer().toLowerCase().contains(s)) ||
                    (item.getEmail() != null && item.getEmail().toLowerCase().contains(s)) ||
                    (item.getNoHp() != null && item.getNoHp().toLowerCase().contains(s)) ||
                    (item.getCabang() != null && item.getCabang().toLowerCase().contains(s))
            ).toList();
        }

        return PageResponse.ofList(all, page, size);
    }

    public List<MarketingPengajuanItemResponse> findAll(String statusFilter) {
        return findAll(statusFilter, null);
    }


    public List<MarketingPengajuanItemResponse> findAll(String statusFilter, UUID karyawanId) {
        UUID branchId = null;
        if (karyawanId != null) {
            Optional<Karyawan> kOpt = karyawanRepository.findById(karyawanId);
            if (kOpt.isPresent() && kOpt.get().getCabang() != null) {
                branchId = kOpt.get().getCabang().getId();
            }
        }

        final UUID filterBranchId = branchId;
        List<PengajuanPinjaman> list = pengajuanRepository.findAllByOrderByCreatedDateDesc();

        return list.stream()
                .filter(p -> filterBranchId == null
                        || (p.getMstBranchId() != null && filterBranchId.equals(p.getMstBranchId())))
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
                        return "MENUNGGU_REVIEW".equalsIgnoreCase(item.getStatus())
                                || "PENDING".equalsIgnoreCase(item.getStatus());
                    }
                    if ("SELESAI_DIREVIEW".equalsIgnoreCase(statusFilter)
                            || "DISETUJUI".equalsIgnoreCase(statusFilter)) {
                        return "SELESAI_DIREVIEW".equalsIgnoreCase(item.getStatus());
                    }
                    if ("PENGAJUAN_DITOLAK".equalsIgnoreCase(statusFilter)
                            || "DITOLAK".equalsIgnoreCase(statusFilter)) {
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
        Optional<AlamatCustomer> alamatDomisiliOpt = alamatRepository.findByCustomer_IdAndJenisAlamat(customerId,
                "DOMISILI");

        // Dokumen Identitas (KTP & Selfie)
        Optional<DokumenCustomer> ktpDocOpt = dokumenCustomerRepository.findByCustomer_IdAndDocType(customerId, "KTP");
        Optional<DokumenCustomer> selfieDocOpt = dokumenCustomerRepository.findByCustomer_IdAndDocType(customerId,
                "SELFIE");

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
                .cicilanBerjalan(scoringOpt.map(ScoringCustomer::getTotalCicilanLainBulanan).orElse(null))
                .skor(analysis != null ? analysis.getSkor() : (scoringOpt.map(ScoringCustomer::getSkor).orElse(null)))
                .statusScoring(analysis != null ? analysis.getStatusScoring()
                        : (scoringOpt.map(ScoringCustomer::getStatusScoring).orElse(null)))
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

        UUID userBranchId = karyawan.getCabang() != null ? karyawan.getCabang().getId() : null;
        if (userBranchId != null && pengajuan.getMstBranchId() != null
                && !userBranchId.equals(pengajuan.getMstBranchId())) {
            throw new BussinessRuleException("Anda tidak memiliki izin untuk mereview pengajuan dari cabang lain");
        }

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

        // Kirim notifikasi ke customer
        if ("DISETUJUI".equals(hasilReview)) {
            notifikasiService.createNotification(
                    pengajuan.getMstCustomerId(),
                    pengajuan.getId(),
                    "REVIEW_MARKETING",
                    "IN_APP",
                    "Review Pinjaman Disetujui",
                    "Pengajuan pinjaman no. " + pengajuan.getNomorPengajuan()
                            + " telah disetujui pada tahap review Marketing dan diteruskan ke Branch Manager.");
        } else if ("DITOLAK".equals(hasilReview)) {
            notifikasiService.createNotification(
                    pengajuan.getMstCustomerId(),
                    pengajuan.getId(),
                    "REVIEW_MARKETING",
                    "IN_APP",
                    "Pengajuan Pinjaman Ditolak",
                    "Pengajuan pinjaman no. " + pengajuan.getNomorPengajuan()
                            + " tidak disetujui pada tahap review Marketing. Catatan: " + request.getCatatan());
        } else if ("PERLU_REVISI".equals(hasilReview)) {
            notifikasiService.createNotification(
                    pengajuan.getMstCustomerId(),
                    pengajuan.getId(),
                    "REVIEW_MARKETING",
                    "IN_APP",
                    "Perlu Revisi Dokumen Pinjaman",
                    "Pengajuan pinjaman no. " + pengajuan.getNomorPengajuan()
                            + " memerlukan perbaikan dokumen. Catatan: " + request.getCatatan());
        }

        if (auditLogService != null && karyawanId != null) {
            String desc = "Marketing " + karyawan.getNama() + " mereview pengajuan no. " + pengajuan.getNomorPengajuan()
                    + " dengan hasil: " + hasilReview;
            auditLogService.recordLog(karyawanId, "REVIEW", "PENGAJUAN", desc);
        }

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
        if ("SELESAI_DIREVIEW".equalsIgnoreCase(rawStatus) || "DISETUJUI".equalsIgnoreCase(rawStatus)
                || "APPROVED".equalsIgnoreCase(rawStatus)) {
            return "SELESAI_DIREVIEW";
        }
        if ("PENGAJUAN_DITOLAK".equalsIgnoreCase(rawStatus) || "REJECTED".equalsIgnoreCase(rawStatus)
                || "DITOLAK".equalsIgnoreCase(rawStatus)) {
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

    private BigDecimal calculateEstimasiAngsuran(BigDecimal jumlahPinjaman, Integer tenorBulan,
            BigDecimal bungaTahunan) {
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

    public MarketingDashboardStatsResponse getDashboardStats() {
        return getDashboardStats(null);
    }

    public MarketingDashboardStatsResponse getDashboardStats(UUID karyawanId) {
        UUID branchId = null;
        if (karyawanId != null) {
            Optional<Karyawan> kOpt = karyawanRepository.findById(karyawanId);
            if (kOpt.isPresent() && kOpt.get().getCabang() != null) {
                branchId = kOpt.get().getCabang().getId();
            }
        }

        final UUID filterBranchId = branchId;
        List<PengajuanPinjaman> allLoans = pengajuanRepository.findAll().stream()
                .filter(p -> filterBranchId == null
                        || (p.getMstBranchId() != null && filterBranchId.equals(p.getMstBranchId())))
                .toList();

        long total = allLoans.size();
        long menungguReview = 0;
        long perluRevisi = 0;
        long disetujuiMarketing = 0;
        long ditolakMarketing = 0;

        java.util.Map<String, Long> statusMap = new java.util.LinkedHashMap<>();
        statusMap.put("MENUNGGU_REVIEW", 0L);
        statusMap.put("PERLU_REVISI", 0L);
        statusMap.put("SELESAI_DIREVIEW", 0L);
        statusMap.put("PENGAJUAN_DITOLAK", 0L);
        statusMap.put("DICAIRKAN", 0L);

        for (PengajuanPinjaman p : allLoans) {
            String status = p.getStatusPengajuan() != null ? p.getStatusPengajuan().toUpperCase() : "PENDING";
            if ("PENDING".equals(status) || "MENUNGGU_REVIEW".equals(status)) {
                menungguReview++;
                statusMap.put("MENUNGGU_REVIEW", statusMap.get("MENUNGGU_REVIEW") + 1);
            } else if ("PERLU_REVISI".equals(status)) {
                perluRevisi++;
                statusMap.put("PERLU_REVISI", statusMap.get("PERLU_REVISI") + 1);
            } else if ("SELESAI_DIREVIEW".equals(status) || "PENGAJUAN_DISETUJUI".equals(status)
                    || "DICAIRKAN".equals(status) || "APPROVED".equals(status)) {
                disetujuiMarketing++;
                if ("DICAIRKAN".equals(status)) {
                    statusMap.put("DICAIRKAN", statusMap.get("DICAIRKAN") + 1);
                } else {
                    statusMap.put("SELESAI_DIREVIEW", statusMap.get("SELESAI_DIREVIEW") + 1);
                }
            } else if ("PENGAJUAN_DITOLAK".equals(status) || "DITOLAK".equals(status) || "REJECTED".equals(status)) {
                ditolakMarketing++;
                statusMap.put("PENGAJUAN_DITOLAK", statusMap.get("PENGAJUAN_DITOLAK") + 1);
            }
        }

        long totalDecided = disetujuiMarketing + ditolakMarketing;
        double approvalRate = totalDecided > 0 ? ((double) disetujuiMarketing / totalDecided) * 100.0 : 0.0;
        approvalRate = Math.round(approvalRate * 10.0) / 10.0;

        // Scoring Distribution
        List<ScoringCustomer> scorings = scoringRepository.findAll();
        long skorTinggi = 0; // >= 75
        long skorSedang = 0; // 60 - 74
        long skorRendah = 0; // < 60

        for (ScoringCustomer s : scorings) {
            Integer score = s.getSkor();
            if (score != null) {
                if (score >= 75) {
                    skorTinggi++;
                } else if (score >= 60) {
                    skorSedang++;
                } else {
                    skorRendah++;
                }
            }
        }

        java.util.Map<String, Long> scoringMap = new java.util.LinkedHashMap<>();
        scoringMap.put("SKOR_TINGGI (>= 75)", skorTinggi);
        scoringMap.put("SKOR_SEDANG (60 - 74)", skorSedang);
        scoringMap.put("SKOR_RENDAH (< 60)", skorRendah);

        // Weekly Trends (Last 7 days)
        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.format.DateTimeFormatter dateFormatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");
        java.time.format.DateTimeFormatter dayFormatter = java.time.format.DateTimeFormatter.ofPattern("EEE",
                java.util.Locale.forLanguageTag("id-ID"));

        java.util.List<MarketingDashboardStatsResponse.DailyTrendItem> weeklyTrends = new java.util.ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            java.time.LocalDate d = today.minusDays(i);
            long count = 0;
            BigDecimal nominal = BigDecimal.ZERO;

            for (PengajuanPinjaman p : allLoans) {
                if (p.getCreatedDate() != null && p.getCreatedDate().toLocalDate().isEqual(d)) {
                    count++;
                    if (p.getJumlahPinjaman() != null) {
                        nominal = nominal.add(p.getJumlahPinjaman());
                    }
                }
            }

            weeklyTrends.add(MarketingDashboardStatsResponse.DailyTrendItem.builder()
                    .date(d.format(dateFormatter))
                    .day(d.format(dayFormatter))
                    .count(count)
                    .totalNominal(nominal)
                    .build());
        }

        return MarketingDashboardStatsResponse.builder()
                .totalPengajuan(total)
                .menungguReview(menungguReview)
                .perluRevisi(perluRevisi)
                .disetujuiMarketing(disetujuiMarketing)
                .ditolakMarketing(ditolakMarketing)
                .approvalRate(approvalRate)
                .scoringDistribution(scoringMap)
                .statusDistribution(statusMap)
                .weeklyTrends(weeklyTrends)
                .build();
    }
}
