package com.bcafinance.backend_saku.features.superadmin.monitoring.service;

import com.bcafinance.backend_saku.core.dto.PageResponse;
import com.bcafinance.backend_saku.core.entity.Cabang;
import com.bcafinance.backend_saku.core.entity.Customer;
import com.bcafinance.backend_saku.core.entity.Pencairan;
import com.bcafinance.backend_saku.core.entity.PengajuanPinjaman;
import com.bcafinance.backend_saku.core.entity.Persetujuan;
import com.bcafinance.backend_saku.core.entity.ReviewPengajuan;
import com.bcafinance.backend_saku.core.repository.CabangRepository;
import com.bcafinance.backend_saku.core.repository.CustomerRepository;
import com.bcafinance.backend_saku.core.repository.PencairanRepository;
import com.bcafinance.backend_saku.core.repository.PengajuanPinjamanRepository;
import com.bcafinance.backend_saku.core.repository.PersetujuanRepository;
import com.bcafinance.backend_saku.core.repository.ReviewPengajuanRepository;
import com.bcafinance.backend_saku.core.util.LoanStatusHelper;
import com.bcafinance.backend_saku.features.marketing.dto.MarketingPengajuanDetailResponse;
import com.bcafinance.backend_saku.features.marketing.service.MarketingReviewService;
import com.bcafinance.backend_saku.features.superadmin.monitoring.dto.MonitoringPengajuanDetailResponse;
import com.bcafinance.backend_saku.features.superadmin.monitoring.dto.MonitoringPengajuanItemResponse;
import com.bcafinance.backend_saku.features.superadmin.monitoring.dto.RiwayatRoleStatusResponse;
import com.bcafinance.backend_saku.features.superadmin.monitoring.dto.RoleStatusResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MonitoringPengajuanService {

    private final PengajuanPinjamanRepository pengajuanRepository;
    private final CustomerRepository customerRepository;
    private final CabangRepository cabangRepository;
    private final ReviewPengajuanRepository reviewPengajuanRepository;
    private final PersetujuanRepository persetujuanRepository;
    private final PencairanRepository pencairanRepository;
    private final MarketingReviewService marketingReviewService;

    @Transactional(readOnly = true)
    public PageResponse<MonitoringPengajuanItemResponse> findAllPaginated(
            int page, int size, String search, String statusFilter, UUID branchId) {

        List<MonitoringPengajuanItemResponse> all = findAll(statusFilter, branchId);

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

    @Transactional(readOnly = true)
    public List<MonitoringPengajuanItemResponse> findAll(String statusFilter, UUID branchId) {
        List<PengajuanPinjaman> list = pengajuanRepository.findAllByOrderByCreatedDateDesc();

        return list.stream()
                .filter(p -> branchId == null || (p.getMstBranchId() != null && branchId.equals(p.getMstBranchId())))
                .map(this::mapToItemResponse)
                .filter(item -> filterByStatus(item, statusFilter))
                .toList();
    }

    @Transactional(readOnly = true)
    public MonitoringPengajuanDetailResponse getDetail(UUID pengajuanId) {
        MarketingPengajuanDetailResponse base = marketingReviewService.getDetail(pengajuanId);
        Optional<PengajuanPinjaman> pengajuanOpt = pengajuanRepository.findById(pengajuanId);

        RoleStatusResponse marketingStage = RoleStatusResponse.builder().status("MENUNGGU_REVIEW").tanggal(null).build();
        RoleStatusResponse bmStage = RoleStatusResponse.builder().status("-").tanggal(null).build();
        RoleStatusResponse boStage = RoleStatusResponse.builder().status("-").tanggal(null).build();
        String globalStatus = "MENUNGGU_REVIEW_MARKETING";
        String globalStatusLabel = "Menunggu Review Marketing";
        List<RiwayatRoleStatusResponse> riwayatList = new ArrayList<>();

        if (pengajuanOpt.isPresent()) {
            MonitoringPengajuanItemResponse item = mapToItemResponse(pengajuanOpt.get());
            globalStatus = item.getStatus();
            globalStatusLabel = item.getStatusLabel();
            marketingStage = item.getMarketing();
            bmStage = item.getBranchManager();
            boStage = item.getBackoffice();
            riwayatList = item.getRiwayat();
        }

        return MonitoringPengajuanDetailResponse.builder()
                .pengajuanId(base.getPengajuanId())
                .nomorPengajuan(base.getNomorPengajuan())
                .tanggalPengajuan(base.getTanggalPengajuan())
                .statusPengajuan(globalStatus)
                .statusPengajuanLabel(globalStatusLabel)
                // Data Customer
                .customerId(base.getCustomerId())
                .namaLengkap(base.getNamaLengkap())
                .email(base.getEmail())
                .nik(base.getNik())
                .noHp(base.getNoHp())
                .pekerjaan(base.getPekerjaan())
                .tempatKerja(base.getTempatKerja())
                .statusPekerjaan(base.getStatusPekerjaan())
                .pendapatan(base.getPendapatan())
                .penghasilanBulanan(base.getPenghasilanBulanan())
                .namaBank(base.getNamaBank())
                .noRekening(base.getNoRekening())
                .namaRekening(base.getNamaRekening())
                .alamatKtp(base.getAlamatKtp())
                .alamatDomisili(base.getAlamatDomisili())
                // Dokumen
                .fotoSelfie(base.getFotoSelfie())
                .fotoKtp(base.getFotoKtp())
                .slipGaji(base.getSlipGaji())
                .rekeningKoran(base.getRekeningKoran())
                .npwp(base.getNpwp())
                .dokumenPinjamanList(base.getDokumenPinjamanList())
                // Scoring
                .scoringStatusPekerjaan(base.getScoringStatusPekerjaan())
                .scoringPenghasilan(base.getScoringPenghasilan())
                .lamaBekerjaBulan(base.getLamaBekerjaBulan())
                .cicilanBerjalan(base.getCicilanBerjalan())
                .skor(base.getSkor())
                .statusScoring(base.getStatusScoring())
                .keputusanSistem(base.getKeputusanSistem())
                .dbr(base.getDbr())
                .dbrPercentage(base.getDbrPercentage())
                .plafonNama(base.getPlafonNama())
                .plafonMaksimal(base.getPlafonMaksimal())
                .estimasiPlafondDisetujui(base.getEstimasiPlafondDisetujui())
                .isAmbigu(base.getIsAmbigu())
                .notesAmbigu(base.getNotesAmbigu())
                .ringkasanAnalisis(base.getRingkasanAnalisis())
                .breakdown(base.getBreakdown())
                // Pinjaman
                .jumlahPinjaman(base.getJumlahPinjaman())
                .tenorBulan(base.getTenorBulan())
                .tujuanPinjaman(base.getTujuanPinjaman())
                .bunga(base.getBunga())
                .biayaAdmin(base.getBiayaAdmin())
                .estimasiCicilan(base.getEstimasiCicilan())
                .branchId(base.getBranchId())
                .namaCabang(base.getNamaCabang())
                .kotaCabang(base.getKotaCabang())
                // Stages & Riwayat (status dan tanggal saja)
                .marketing(marketingStage)
                .branchManager(bmStage)
                .backoffice(boStage)
                .riwayat(riwayatList)
                .build();
    }

    private MonitoringPengajuanItemResponse mapToItemResponse(PengajuanPinjaman p) {
        Customer customer = customerRepository.findById(p.getMstCustomerId()).orElse(null);
        Cabang cabang = cabangRepository.findById(p.getMstBranchId()).orElse(null);

        // Lifecycle Stages
        Optional<ReviewPengajuan> latestReview = reviewPengajuanRepository
                .findFirstByTrxPengajuanPinjamanIdOrderByCreatedDateDesc(p.getId());
        Optional<Persetujuan> latestApproval = persetujuanRepository
                .findFirstByTrxPengajuanPinjamanIdOrderByCreatedDateDesc(p.getId());
        Optional<Pencairan> latestPencairan = pencairanRepository
                .findFirstByTrxPengajuanPinjamanIdOrderByCreatedDateDesc(p.getId());

        // 1. Tahap Marketing
        String statusMarketing = "MENUNGGU_REVIEW";
        LocalDateTime tanggalMarketing = p.getCreatedDate();

        if (latestReview.isPresent()) {
            ReviewPengajuan rev = latestReview.get();
            statusMarketing = rev.getHasilReview();
            tanggalMarketing = rev.getCreatedDate();

            if ("PENDING".equalsIgnoreCase(p.getStatusPengajuan()) && "PERLU_REVISI".equalsIgnoreCase(rev.getHasilReview())) {
                statusMarketing = "DOKUMEN_DIREVISI";
                tanggalMarketing = p.getUpdatedDate();
            }
        }

        // 2. Tahap Branch Manager
        String statusBranchManager = "-";
        LocalDateTime tanggalBranchManager = null;

        if (latestApproval.isPresent()) {
            Persetujuan ap = latestApproval.get();
            statusBranchManager = ap.getHasilPersetujuan();
            tanggalBranchManager = ap.getCreatedDate();
        } else if ("DISETUJUI".equalsIgnoreCase(statusMarketing) || "SELESAI_DIREVIEW".equalsIgnoreCase(statusMarketing)) {
            statusBranchManager = "MENUNGGU_PERSETUJUAN";
            tanggalBranchManager = tanggalMarketing;
        }

        // 3. Tahap Backoffice Pencairan
        String statusBackoffice = "-";
        LocalDateTime tanggalBackoffice = null;

        if (latestPencairan.isPresent()) {
            Pencairan pc = latestPencairan.get();
            statusBackoffice = pc.getStatusPencairan();
            tanggalBackoffice = pc.getCreatedDate();
        } else if ("DISETUJUI".equalsIgnoreCase(statusBranchManager)) {
            statusBackoffice = "MENUNGGU_PENCAIRAN";
            tanggalBackoffice = tanggalBranchManager;
        }

        // Global status & label via LoanStatusHelper
        String globalStatus = LoanStatusHelper.determineGlobalStatus(p, latestReview, latestApproval, latestPencairan);
        String globalStatusLabel = LoanStatusHelper.getStatusLabel(globalStatus);

        // Role Status Objects
        RoleStatusResponse marketingInfo = RoleStatusResponse.builder()
                .status(statusMarketing)
                .tanggal(tanggalMarketing)
                .build();

        RoleStatusResponse bmInfo = RoleStatusResponse.builder()
                .status(statusBranchManager)
                .tanggal(tanggalBranchManager)
                .build();

        RoleStatusResponse boInfo = RoleStatusResponse.builder()
                .status(statusBackoffice)
                .tanggal(tanggalBackoffice)
                .build();

        // Riwayat List (role, status, tanggal saja)
        List<RiwayatRoleStatusResponse> riwayat = buildRiwayatList(marketingInfo, bmInfo, boInfo);

        return MonitoringPengajuanItemResponse.builder()
                .pengajuanId(p.getId())
                .noPengajuan(p.getNomorPengajuan())
                .customerId(p.getMstCustomerId())
                .customer(customer != null ? customer.getNama() : "-")
                .email(customer != null ? customer.getEmail() : "-")
                .noHp(customer != null ? customer.getNoHp() : "-")
                .nik(customer != null ? customer.getNik() : "-")
                .tanggalPengajuan(p.getCreatedDate())
                .jumlah(p.getJumlahPinjaman())
                .tenor(p.getTenorBulan())
                .cabang(cabang != null ? cabang.getNama() : "-")
                .status(globalStatus)
                .statusLabel(globalStatusLabel)
                // Role Stages
                .marketing(marketingInfo)
                .branchManager(bmInfo)
                .backoffice(boInfo)
                // Riwayat
                .riwayat(riwayat)
                .build();
    }

    private List<RiwayatRoleStatusResponse> buildRiwayatList(
            RoleStatusResponse marketing,
            RoleStatusResponse branchManager,
            RoleStatusResponse backoffice) {
        List<RiwayatRoleStatusResponse> list = new ArrayList<>();

        if (marketing != null && marketing.getStatus() != null && !"-".equals(marketing.getStatus())) {
            list.add(RiwayatRoleStatusResponse.builder()
                    .role("MARKETING")
                    .status(marketing.getStatus())
                    .tanggal(marketing.getTanggal())
                    .build());
        }

        if (branchManager != null && branchManager.getStatus() != null && !"-".equals(branchManager.getStatus())) {
            list.add(RiwayatRoleStatusResponse.builder()
                    .role("BRANCH_MANAGER")
                    .status(branchManager.getStatus())
                    .tanggal(branchManager.getTanggal())
                    .build());
        }

        if (backoffice != null && backoffice.getStatus() != null && !"-".equals(backoffice.getStatus())) {
            list.add(RiwayatRoleStatusResponse.builder()
                    .role("BACKOFFICE")
                    .status(backoffice.getStatus())
                    .tanggal(backoffice.getTanggal())
                    .build());
        }

        return list;
    }

    private boolean filterByStatus(MonitoringPengajuanItemResponse item, String statusFilter) {
        if (statusFilter == null || statusFilter.isBlank() || "ALL".equalsIgnoreCase(statusFilter)) {
            return true;
        }
        String status = item.getStatus() != null ? item.getStatus().toUpperCase() : "";
        String sf = statusFilter.trim().toUpperCase();

        if (sf.equals(status)) {
            return true;
        }
        if ("MENUNGGU_REVIEW".equals(sf) || "MENUNGGU_REVIEW_MARKETING".equals(sf)) {
            return status.equals(LoanStatusHelper.MENUNGGU_REVIEW_MARKETING) || status.equals("MENUNGGU_REVIEW") || status.equals("PENDING");
        }
        if ("MENUNGGU_PERSETUJUAN".equals(sf) || "MENUNGGU_PERSETUJUAN_BM".equals(sf) || "SELESAI_DIREVIEW".equals(sf)) {
            return status.equals(LoanStatusHelper.MENUNGGU_PERSETUJUAN_BM) || status.equals("MENUNGGU_PERSETUJUAN") || status.equals("SELESAI_DIREVIEW");
        }
        if ("MENUNGGU_PENCAIRAN".equals(sf) || "PENGAJUAN_DISETUJUI".equals(sf) || "DISETUJUI".equals(sf)) {
            return status.equals(LoanStatusHelper.MENUNGGU_PENCAIRAN) || status.equals("PENGAJUAN_DISETUJUI") || status.equals("DISETUJUI");
        }
        if ("DICAIRKAN".equals(sf)) {
            return status.equals(LoanStatusHelper.DICAIRKAN) || status.equals("DISBURSED");
        }
        if ("DITOLAK".equals(sf)) {
            return status.contains("TOLAK") || status.contains("REJECT");
        }
        if ("PERLU_REVISI".equals(sf)) {
            return status.equals(LoanStatusHelper.PERLU_REVISI) || status.equals(LoanStatusHelper.DOKUMEN_DIREVISI);
        }
        return false;
    }
}
