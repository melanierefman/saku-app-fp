package com.bcafinance.backend_saku.features.superadmin.dashboard.service;

import com.bcafinance.backend_saku.core.entity.AuditLog;
import com.bcafinance.backend_saku.core.entity.Cabang;
import com.bcafinance.backend_saku.core.entity.Customer;
import com.bcafinance.backend_saku.core.entity.Karyawan;
import com.bcafinance.backend_saku.core.entity.PengajuanPinjaman;
import com.bcafinance.backend_saku.core.repository.AuditLogRepository;
import com.bcafinance.backend_saku.core.repository.CabangRepository;
import com.bcafinance.backend_saku.core.repository.CustomerRepository;
import com.bcafinance.backend_saku.core.repository.KaryawanRepository;
import com.bcafinance.backend_saku.core.repository.PengajuanPinjamanRepository;
import com.bcafinance.backend_saku.core.entity.Pencairan;
import com.bcafinance.backend_saku.core.entity.Persetujuan;
import com.bcafinance.backend_saku.core.entity.ReviewPengajuan;
import com.bcafinance.backend_saku.core.repository.PencairanRepository;
import com.bcafinance.backend_saku.core.repository.PersetujuanRepository;
import com.bcafinance.backend_saku.core.repository.ReviewPengajuanRepository;
import com.bcafinance.backend_saku.core.util.LoanStatusHelper;
import com.bcafinance.backend_saku.features.superadmin.dashboard.dto.SuperadminDashboardStatsResponse;
import com.bcafinance.backend_saku.features.superadmin.dashboard.dto.SuperadminDashboardStatsResponse.BranchPerformanceItem;
import com.bcafinance.backend_saku.features.superadmin.dashboard.dto.SuperadminDashboardStatsResponse.MonthlyLoanTrendItem;
import com.bcafinance.backend_saku.features.superadmin.dashboard.dto.SuperadminDashboardStatsResponse.RecentAuditLogItem;
import com.bcafinance.backend_saku.features.superadmin.dashboard.dto.SuperadminDashboardStatsResponse.RecentPengajuanItem;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SuperadminDashboardService {

    private final PengajuanPinjamanRepository pengajuanRepository;
    private final CustomerRepository customerRepository;
    private final CabangRepository cabangRepository;
    private final KaryawanRepository karyawanRepository;
    private final AuditLogRepository auditLogRepository;
    private final ReviewPengajuanRepository reviewPengajuanRepository;
    private final PersetujuanRepository persetujuanRepository;
    private final PencairanRepository pencairanRepository;

    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("MMM yyyy");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public SuperadminDashboardStatsResponse getDashboardStats() {
        List<PengajuanPinjaman> allPengajuan = pengajuanRepository.findAllByOrderByCreatedDateDesc();
        List<Cabang> allCabang = cabangRepository.findAll();
        Map<UUID, Cabang> cabangMap = allCabang.stream()
                .collect(Collectors.toMap(Cabang::getId, c -> c, (a, b) -> a));

        List<Customer> allCustomers = customerRepository.findAll();
        Map<UUID, Customer> customerMap = allCustomers.stream()
                .collect(Collectors.toMap(Customer::getId, c -> c, (a, b) -> a));

        List<Karyawan> allKaryawan = karyawanRepository.findAll();
        Map<UUID, Karyawan> karyawanMap = allKaryawan.stream()
                .collect(Collectors.toMap(Karyawan::getId, k -> k, (a, b) -> a));

        // 1. Hitung Status Funnel
        long totalPengajuan = allPengajuan.size();
        long menungguMarketing = 0;
        long menungguBM = 0;
        long menungguPencairan = 0;
        long telahDicairkan = 0;
        long ditolak = 0;

        BigDecimal totalNominalDiajukan = BigDecimal.ZERO;
        BigDecimal totalNominalDicairkan = BigDecimal.ZERO;

        Map<String, Long> statusDistribution = new LinkedHashMap<>();

        for (PengajuanPinjaman p : allPengajuan) {
            Optional<ReviewPengajuan> latestRev = reviewPengajuanRepository
                    .findFirstByTrxPengajuanPinjamanIdOrderByCreatedDateDesc(p.getId());
            Optional<Persetujuan> latestApp = persetujuanRepository
                    .findFirstByTrxPengajuanPinjamanIdOrderByCreatedDateDesc(p.getId());
            Optional<Pencairan> latestPen = pencairanRepository
                    .findFirstByTrxPengajuanPinjamanIdOrderByCreatedDateDesc(p.getId());

            String status = LoanStatusHelper.determineGlobalStatus(p, latestRev, latestApp, latestPen);
            statusDistribution.put(status, statusDistribution.getOrDefault(status, 0L) + 1L);

            BigDecimal nominal = p.getJumlahPinjaman() != null ? p.getJumlahPinjaman() : BigDecimal.ZERO;
            totalNominalDiajukan = totalNominalDiajukan.add(nominal);

            switch (status) {
                case LoanStatusHelper.DITOLAK_MARKETING, LoanStatusHelper.DITOLAK_BM, "DITOLAK" -> ditolak++;
                case LoanStatusHelper.DICAIRKAN -> {
                    telahDicairkan++;
                    totalNominalDicairkan = totalNominalDicairkan.add(nominal);
                }
                case LoanStatusHelper.MENUNGGU_PENCAIRAN -> menungguPencairan++;
                case LoanStatusHelper.MENUNGGU_PERSETUJUAN_BM -> menungguBM++;
                default -> menungguMarketing++;
            }
        }

        // 2. Approval Rate Nasional
        double approvalRate = totalPengajuan > 0
                ? ((double) (telahDicairkan + menungguPencairan) / totalPengajuan) * 100.0
                : 0.0;
        approvalRate = Math.round(approvalRate * 10.0) / 10.0;

        // 3. Branch Performance
        Map<UUID, List<PengajuanPinjaman>> perBranch = allPengajuan.stream()
                .filter(p -> p.getMstBranchId() != null)
                .collect(Collectors.groupingBy(PengajuanPinjaman::getMstBranchId));

        List<BranchPerformanceItem> branchPerformance = new ArrayList<>();
        for (Cabang c : allCabang) {
            List<PengajuanPinjaman> branchLoans = perBranch.getOrDefault(c.getId(), List.of());
            long count = branchLoans.size();
            BigDecimal totalNom = branchLoans.stream()
                    .map(p -> p.getJumlahPinjaman() != null ? p.getJumlahPinjaman() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            long disetujui = branchLoans.stream()
                    .filter(p -> {
                        String s = p.getStatusPengajuan() != null ? p.getStatusPengajuan().toUpperCase() : "";
                        return s.contains("SETUJU") || s.contains("APPROV") || s.contains("CAIR");
                    })
                    .count();

            branchPerformance.add(BranchPerformanceItem.builder()
                    .branchId(c.getId().toString())
                    .branchName(c.getNama())
                    .kota(c.getKota())
                    .totalPengajuan(count)
                    .totalNominal(totalNom)
                    .totalDisetujui(disetujui)
                    .build());
        }
        branchPerformance.sort(Comparator.comparingLong(BranchPerformanceItem::getTotalPengajuan).reversed());

        // 4. Monthly Trends (6 Bulan Terakhir)
        Map<String, MonthlyLoanTrendItem> trendMap = new LinkedHashMap<>();
        for (PengajuanPinjaman p : allPengajuan) {
            if (p.getCreatedDate() != null) {
                String monthKey = p.getCreatedDate().format(MONTH_FORMATTER);
                BigDecimal nom = p.getJumlahPinjaman() != null ? p.getJumlahPinjaman() : BigDecimal.ZERO;
                String st = p.getStatusPengajuan() != null ? p.getStatusPengajuan().toUpperCase() : "";
                boolean isDisbursed = st.equals("DICAIRKAN") || st.equals("DISBURSED");

                trendMap.compute(monthKey, (k, v) -> {
                    if (v == null) {
                        return MonthlyLoanTrendItem.builder()
                                .month(monthKey)
                                .totalPengajuan(1)
                                .totalNominalDiajukan(nom)
                                .totalNominalDicairkan(isDisbursed ? nom : BigDecimal.ZERO)
                                .build();
                    } else {
                        v.setTotalPengajuan(v.getTotalPengajuan() + 1);
                        v.setTotalNominalDiajukan(v.getTotalNominalDiajukan().add(nom));
                        if (isDisbursed) {
                            v.setTotalNominalDicairkan(v.getTotalNominalDicairkan().add(nom));
                        }
                        return v;
                    }
                });
            }
        }
        List<MonthlyLoanTrendItem> monthlyTrends = new ArrayList<>(trendMap.values());

        // 5. Recent Pengajuan (Top 5)
        List<RecentPengajuanItem> recentPengajuan = allPengajuan.stream()
                .limit(5)
                .map(p -> {
                    Customer cust = customerMap.get(p.getMstCustomerId());
                    Cabang cab = cabangMap.get(p.getMstBranchId());
                    Optional<ReviewPengajuan> latestRev = reviewPengajuanRepository
                            .findFirstByTrxPengajuanPinjamanIdOrderByCreatedDateDesc(p.getId());
                    Optional<Persetujuan> latestApp = persetujuanRepository
                            .findFirstByTrxPengajuanPinjamanIdOrderByCreatedDateDesc(p.getId());
                    Optional<Pencairan> latestPen = pencairanRepository
                            .findFirstByTrxPengajuanPinjamanIdOrderByCreatedDateDesc(p.getId());

                    String gStatus = LoanStatusHelper.determineGlobalStatus(p, latestRev, latestApp, latestPen);
                    String gStatusLabel = LoanStatusHelper.getStatusLabel(gStatus);

                    return RecentPengajuanItem.builder()
                            .id(p.getId().toString())
                            .nomorPengajuan(p.getNomorPengajuan())
                            .namaCustomer(cust != null ? cust.getNama() : "-")
                            .namaCabang(cab != null ? cab.getNama() : "-")
                            .jumlahPinjaman(p.getJumlahPinjaman())
                            .tenorBulan(p.getTenorBulan())
                            .statusPengajuan(gStatus)
                            .statusPengajuanLabel(gStatusLabel)
                            .tanggalPengajuan(p.getCreatedDate() != null ? p.getCreatedDate().format(DATE_TIME_FORMATTER) : "-")
                            .build();
                })
                .toList();

        // 6. Recent Audit Logs (Top 5)
        List<AuditLog> recentLogs = auditLogRepository.findAllByOrderByCreatedDateDesc(PageRequest.of(0, 5)).getContent();
        List<RecentAuditLogItem> recentAuditLogs = recentLogs.stream()
                .map(log -> {
                    Karyawan kary = karyawanMap.get(log.getMstKaryawanId());
                    String username = kary != null ? kary.getUsername() : "System";
                    String roleName = kary != null && kary.getRole() != null ? kary.getRole().getNama() : "-";
                    return RecentAuditLogItem.builder()
                            .id(log.getId().toString())
                            .username(username)
                            .role(roleName)
                            .action(log.getAction())
                            .entity(log.getEntity())
                            .deskripsi(log.getDescription())
                            .timestamp(log.getCreatedDate() != null ? log.getCreatedDate().format(DATE_TIME_FORMATTER) : "-")
                            .build();
                })
                .toList();

        return SuperadminDashboardStatsResponse.builder()
                .totalPengajuan(totalPengajuan)
                .totalNominalDiajukan(totalNominalDiajukan)
                .totalNominalDicairkan(totalNominalDicairkan)
                .totalCustomer((long) allCustomers.size())
                .totalCabang((long) allCabang.size())
                .totalKaryawan((long) allKaryawan.size())
                .approvalRateNasional(approvalRate)
                .menungguReviewMarketing(menungguMarketing)
                .menungguPersetujuanBM(menungguBM)
                .menungguPencairan(menungguPencairan)
                .telahDicairkan(telahDicairkan)
                .ditolak(ditolak)
                .statusDistribution(statusDistribution)
                .branchPerformance(branchPerformance)
                .monthlyTrends(monthlyTrends)
                .recentPengajuan(recentPengajuan)
                .recentAuditLogs(recentAuditLogs)
                .build();
    }
}
