package com.bcafinance.backend_saku.features.customer.service;

import com.bcafinance.backend_saku.core.entity.Angsuran;
import com.bcafinance.backend_saku.core.entity.PengajuanPinjaman;
import com.bcafinance.backend_saku.core.entity.Plafond;
import com.bcafinance.backend_saku.core.entity.ScoringCustomer;
import com.bcafinance.backend_saku.core.repository.AngsuranRepository;
import com.bcafinance.backend_saku.core.repository.PengajuanPinjamanRepository;
import com.bcafinance.backend_saku.core.repository.PlafondRepository;
import com.bcafinance.backend_saku.core.repository.ScoringCustomerRepository;
import com.bcafinance.backend_saku.features.scoring.service.PlafondCalculator;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerPlafondService {

    private final ScoringCustomerRepository scoringRepository;
    private final PlafondRepository plafondRepository;
    private final PengajuanPinjamanRepository pengajuanRepository;
    private final AngsuranRepository angsuranRepository;

    public record CustomerPlafondSummary(
            BigDecimal totalPlafond,
            BigDecimal usedPlafond,
            BigDecimal availablePlafond,
            String tierName,
            BigDecimal sukuBunga,
            BigDecimal biayaAdmin
    ) {
        public CustomerPlafondSummary(BigDecimal totalPlafond, BigDecimal usedPlafond, BigDecimal availablePlafond) {
            this(totalPlafond, usedPlafond, availablePlafond, "Reguler", BigDecimal.valueOf(5.0), BigDecimal.valueOf(250_000));
        }
    }

    // Menghitung ringkasan batas limit dan pemakaian plafond nasabah
    public CustomerPlafondSummary calculatePlafondSummary(UUID customerId) {
        if (customerId == null) {
            return new CustomerPlafondSummary(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, "Reguler", BigDecimal.valueOf(5.0), BigDecimal.valueOf(250_000));
        }

        Optional<ScoringCustomer> scoringOpt = scoringRepository.findFirstByMstCustomerIdOrderByCreatedDateDesc(customerId);
        if (scoringOpt.isEmpty()) {
            return new CustomerPlafondSummary(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, "Reguler", BigDecimal.valueOf(5.0), BigDecimal.valueOf(250_000));
        }

        ScoringCustomer scoring = scoringOpt.get();
        Plafond plafond = resolveCustomerPlafond(scoring);
        BigDecimal totalLimit = resolveTotalPlafondFromPlafond(scoring, plafond);
        BigDecimal usedLimit = calculateUsedPlafond(customerId);
        BigDecimal availableLimit = totalLimit.subtract(usedLimit);
        if (availableLimit.compareTo(BigDecimal.ZERO) < 0) {
            availableLimit = BigDecimal.ZERO;
        }

        String tierName = plafond != null ? plafond.getNama() : "Reguler";
        BigDecimal rawBunga = plafond != null ? plafond.getBunga() : BigDecimal.valueOf(5.0);
        BigDecimal sukuBunga = (rawBunga != null && rawBunga.compareTo(BigDecimal.ONE) <= 0 && rawBunga.compareTo(BigDecimal.ZERO) > 0)
                ? rawBunga.multiply(BigDecimal.valueOf(100))
                : (rawBunga != null ? rawBunga : BigDecimal.valueOf(5.0));
        BigDecimal biayaAdmin = plafond != null && plafond.getBiayaAdmin() != null ? plafond.getBiayaAdmin() : BigDecimal.valueOf(250_000);

        return new CustomerPlafondSummary(
                totalLimit.setScale(2, RoundingMode.HALF_UP),
                usedLimit.setScale(2, RoundingMode.HALF_UP),
                availableLimit.setScale(2, RoundingMode.HALF_UP),
                tierName,
                sukuBunga,
                biayaAdmin
        );
    }

    // Menentukan tier master plafond yang cocok berdasarkan skor kredit dan kapasitas gaji
    public Plafond resolveCustomerPlafond(ScoringCustomer scoring) {
        if (scoring == null) {
            return null;
        }

        BigDecimal pendapatan = scoring.getPenghasilanBulanan() != null ? scoring.getPenghasilanBulanan() : BigDecimal.ZERO;
        List<Plafond> activePlafonds = plafondRepository.findAllByStatusTrue();

        Plafond resolved = null;

        // 1. Prioritaskan pencocokan berdasarkan Rentang Skor (min_skor s/d max_skor) dengan validasi kapasitas pendapatan riil
        if (scoring.getSkor() != null) {
            int skor = scoring.getSkor();
            Optional<Plafond> matchedByScore = activePlafonds.stream()
                    .filter(p -> p.getMinSkor() != null && p.getMaxSkor() != null
                            && skor >= p.getMinSkor() && skor <= p.getMaxSkor())
                    .findFirst();
            if (matchedByScore.isPresent()) {
                Plafond pScore = matchedByScore.get();
                BigDecimal minIncome = pScore.getMinPendapatan() != null ? pScore.getMinPendapatan() : BigDecimal.ZERO;

                // Hard Rule: Jika pendapatan nasabah < syarat minimal tier skornya, sistem otomatis down-tier ke tier tertinggi yang sesuai gajinya
                if (pendapatan.compareTo(minIncome) < 0) {
                    resolved = plafondRepository
                            .findTopByMinPendapatanLessThanEqualAndStatusTrueOrderByMinPendapatanDesc(pendapatan)
                            .orElse(pScore);
                } else {
                    resolved = pScore;
                }
            }
        }

        // 2. Jika skor belum match rentang skor aktif, fallback ke pencocokan berdasarkan pendapatan tertinggi
        if (resolved == null) {
            resolved = plafondRepository
                    .findTopByMinPendapatanLessThanEqualAndStatusTrueOrderByMinPendapatanDesc(pendapatan)
                    .or(() -> plafondRepository.findFirstByStatusTrueOrderByMinSkorAsc())
                    .orElse(null);
        }

        // 3. Fallback terakhir jika belum ada tier aktif yang match sama sekali, gunakan mstPlafondId existing jika ada
        if (resolved == null && scoring.getMstPlafondId() != null) {
            resolved = plafondRepository.findById(scoring.getMstPlafondId()).orElse(null);
        }

        // Sinkronisasi mstPlafondId pada entity scoring jika berbeda
        if (resolved != null && (scoring.getMstPlafondId() == null || !resolved.getId().equals(scoring.getMstPlafondId()))) {
            scoring.setMstPlafondId(resolved.getId());
            try {
                scoringRepository.save(scoring);
            } catch (Exception e) {
                log.warn("Failed to auto-sync mstPlafondId for scoring id {}: {}", scoring.getId(), e.getMessage());
            }
        }

        return resolved;
    }

    // Menghitung besaran nominal total plafond yang disetujui dari kalkulator tier
    private BigDecimal resolveTotalPlafondFromPlafond(ScoringCustomer scoring, Plafond plafond) {
        if (scoring == null) {
            return BigDecimal.ZERO;
        }

        if (plafond == null) {
            return BigDecimal.valueOf(50_000_000);
        }

        int skor = scoring.getSkor() != null ? scoring.getSkor() : 0;
        BigDecimal pendapatan = scoring.getPenghasilanBulanan() != null ? scoring.getPenghasilanBulanan() : BigDecimal.ZERO;
        BigDecimal cicilan = scoring.getTotalCicilanLainBulanan() != null ? scoring.getTotalCicilanLainBulanan() : BigDecimal.ZERO;
        int tenure = scoring.getLamaBekerjaBulan() != null ? scoring.getLamaBekerjaBulan() : 0;

        PlafondCalculator.PersonalizedPlafondResult result = PlafondCalculator.calculate(
                plafond, skor, pendapatan, cicilan, tenure);

        return result.finalApprovedPlafond();
    }

    // Menentukan Total Plafond maksimal yang disetujui berdasarkan scoring nasabah
    public BigDecimal resolveTotalPlafond(ScoringCustomer scoring) {
        if (scoring == null) {
            return BigDecimal.ZERO;
        }
        Plafond plafond = resolveCustomerPlafond(scoring);
        return resolveTotalPlafondFromPlafond(scoring, plafond);
    }

    // Menghitung total pokok pinjaman yang sedang terpakai atau terikat
    public BigDecimal calculateUsedPlafond(UUID customerId) {
        List<PengajuanPinjaman> loans = pengajuanRepository.findAllByMstCustomerIdOrderByCreatedDateDesc(customerId);
        BigDecimal totalUsed = BigDecimal.ZERO;

        Set<String> rejectedStatuses = Set.of(
                "PENGAJUAN_DITOLAK",
                "DITOLAK",
                "REJECTED",
                "DITOLAK_MARKETING",
                "DITOLAK_BM",
                "BATAL",
                "CANCELLED"
        );

        for (PengajuanPinjaman p : loans) {
            String status = p.getStatusPengajuan() != null ? p.getStatusPengajuan().toUpperCase() : "";
            if (rejectedStatuses.contains(status)) {
                // Pengajuan ditolak atau dibatalkan tidak memotong plafon
                continue;
            }

            if ("DICAIRKAN".equals(status) || "DISBURSED".equals(status)) {
                // Pinjaman Aktif: Hitung sisa pokok pinjaman berjalan yang belum lunas
                BigDecimal outstandingPrincipal = calculateOutstandingPrincipalForLoan(p);
                totalUsed = totalUsed.add(outstandingPrincipal);
            } else {
                // Pinjaman dalam pipeline (MENUNGGU_DOKUMEN, PENDING, SELESAI_DIREVIEW, PENGAJUAN_DISETUJUI, PERLU_REVISI, dll.)
                if (p.getJumlahPinjaman() != null) {
                    totalUsed = totalUsed.add(p.getJumlahPinjaman());
                }
            }
        }

        return totalUsed.setScale(2, RoundingMode.HALF_UP);
    }

    // Menghitung sisa pokok pinjaman aktif berdasarkan jadwal angsuran yang telah terbayar
    public BigDecimal calculateOutstandingPrincipalForLoan(PengajuanPinjaman p) {
        if (p.getJumlahPinjaman() == null || p.getJumlahPinjaman().compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        List<Angsuran> installments = angsuranRepository.findAllByTrxPengajuanPinjamanIdOrderByCicilanKeAsc(p.getId());
        if (installments.isEmpty()) {
            return p.getJumlahPinjaman();
        }

        long totalCount = installments.size();
        long lunasCount = installments.stream()
                .filter(a -> "LUNAS".equalsIgnoreCase(a.getStatusBayar()) || "PAID".equalsIgnoreCase(a.getStatusBayar()))
                .count();

        if (lunasCount >= totalCount) {
            // Pinjaman sudah lunas sepenuhnya, pokok terpakai = 0
            return BigDecimal.ZERO;
        }

        int tenor = p.getTenorBulan() != null && p.getTenorBulan() > 0 ? p.getTenorBulan() : (int) totalCount;
        BigDecimal pokokBulanan = p.getJumlahPinjaman().divide(BigDecimal.valueOf(tenor), 2, RoundingMode.HALF_UP);
        BigDecimal pokokTerbayar = pokokBulanan.multiply(BigDecimal.valueOf(lunasCount));

        BigDecimal sisaPokok = p.getJumlahPinjaman().subtract(pokokTerbayar);
        return sisaPokok.compareTo(BigDecimal.ZERO) > 0 ? sisaPokok : BigDecimal.ZERO;
    }

    // Memformat nilai desimal ke string format mata uang Rupiah
    public static String formatRupiah(BigDecimal amount) {
        if (amount == null) {
            return "0";
        }
        return NumberFormat.getIntegerInstance(Locale.of("id", "ID")).format(amount);
    }
}
