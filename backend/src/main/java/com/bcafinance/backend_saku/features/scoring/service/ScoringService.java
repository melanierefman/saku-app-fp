package com.bcafinance.backend_saku.features.scoring.service;

import com.bcafinance.backend_saku.features.scoring.dto.ScoringAnalysisResponse;
import com.bcafinance.backend_saku.features.scoring.dto.ScoringBreakdown;
import com.bcafinance.backend_saku.core.entity.Plafond;
import com.bcafinance.backend_saku.core.entity.ScoringCustomer;
import com.bcafinance.backend_saku.core.repository.PlafondRepository;
import com.bcafinance.backend_saku.core.repository.ScoringCustomerRepository;
import com.bcafinance.backend_saku.features.customer.service.CustomerPlafondService;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScoringService {

    private final PlafondRepository plafondRepository;
    private final CustomerPlafondService customerPlafondService;
    private final ScoringCustomerRepository scoringCustomerRepository;

    public ScoringResult calculateScore(
            BigDecimal totalCicilanLainnya,
            BigDecimal pendapatan,
            int lamaBekerjaBulan,
            String statusPekerjaan) {
        BigDecimal dbr = totalCicilanLainnya.divide(pendapatan, 6, java.math.RoundingMode.HALF_UP);

        double skorAkhir = (scoreDbr(dbr) * 0.35)
                + (scorePendapatan(pendapatan) * 0.25)
                + (scoreLamaBekerja(lamaBekerjaBulan) * 0.25)
                + (scoreStatusPekerjaan(statusPekerjaan) * 0.15);

        return new ScoringResult(skorAkhir, mapSkorToKeputusan(skorAkhir));
    }

    public record ScoringSyncSummary(
            int totalProcessed,
            int totalUpdated,
            String message
    ) {}

    @Transactional
    public ScoringSyncSummary recalculateAllScoring() {
        List<ScoringCustomer> allScoring = scoringCustomerRepository.findAll();
        int updated = 0;
        for (ScoringCustomer scoring : allScoring) {
            BigDecimal pendapatan = scoring.getPenghasilanBulanan() != null ? scoring.getPenghasilanBulanan() : BigDecimal.ZERO;
            BigDecimal totalCicilan = scoring.getTotalCicilanLainBulanan() != null ? scoring.getTotalCicilanLainBulanan() : BigDecimal.ZERO;
            int lamaKerja = scoring.getLamaBekerjaBulan() != null ? scoring.getLamaBekerjaBulan() : 0;
            String statusKerja = scoring.getStatusPekerjaan();

            ScoringResult result = calculateScore(totalCicilan, pendapatan, lamaKerja, statusKerja);
            int newScore = (int) Math.round(result.score());

            scoring.setSkor(newScore);
            scoring.setStatusScoring(result.decision());

            if (customerPlafondService != null) {
                Plafond matchedPlafond = customerPlafondService.resolveCustomerPlafond(scoring);
                if (matchedPlafond != null) {
                    scoring.setMstPlafondId(matchedPlafond.getId());
                }
            }
            scoring.setUpdatedDate(LocalDateTime.now());
            scoringCustomerRepository.save(scoring);
            updated++;
        }
        return new ScoringSyncSummary(allScoring.size(), updated, "Berhasil menyinkronkan " + updated + " data scoring customer.");
    }

    public ScoringAnalysisResponse analyzeScoring(ScoringCustomer scoring) {
        if (scoring == null) {
            return null;
        }

        BigDecimal pendapatan = scoring.getPenghasilanBulanan() != null ? scoring.getPenghasilanBulanan() : BigDecimal.ZERO;
        BigDecimal totalCicilan = scoring.getTotalCicilanLainBulanan() != null ? scoring.getTotalCicilanLainBulanan() : BigDecimal.ZERO;
        int skor = scoring.getSkor() != null ? scoring.getSkor() : 0;
        String statusScoring = scoring.getStatusScoring() != null ? scoring.getStatusScoring() : "PENDING";

        // 1. Calculate DBR
        BigDecimal dbr = BigDecimal.ZERO;
        double dbrPercentage = 0.0;
        if (pendapatan.compareTo(BigDecimal.ZERO) > 0) {
            dbr = totalCicilan.divide(pendapatan, 4, RoundingMode.HALF_UP);
            dbrPercentage = dbr.multiply(BigDecimal.valueOf(100)).doubleValue();
        }

        // 2. Evaluasi Plafond (Konsisten menggunakan resolveCustomerPlafond)
        List<Plafond> activePlafonds = plafondRepository.findAllByStatusTrue();
        Plafond resolvedPlafond = (customerPlafondService != null)
                ? customerPlafondService.resolveCustomerPlafond(scoring)
                : null;
        if (resolvedPlafond == null) {
            Optional<Plafond> matchedByScore = activePlafonds.stream()
                    .filter(p -> p.getMinSkor() != null && p.getMaxSkor() != null
                            && skor >= p.getMinSkor() && skor <= p.getMaxSkor())
                    .findFirst();
            if (matchedByScore.isPresent()) {
                Plafond pScore = matchedByScore.get();
                BigDecimal minIncome = pScore.getMinPendapatan() != null ? pScore.getMinPendapatan() : BigDecimal.ZERO;
                resolvedPlafond = (pendapatan.compareTo(minIncome) < 0)
                        ? plafondRepository.findTopByMinPendapatanLessThanEqualAndStatusTrueOrderByMinPendapatanDesc(pendapatan).orElse(pScore)
                        : pScore;
            } else {
                resolvedPlafond = plafondRepository
                        .findTopByMinPendapatanLessThanEqualAndStatusTrueOrderByMinPendapatanDesc(pendapatan)
                        .or(() -> plafondRepository.findFirstByStatusTrueOrderByMinSkorAsc())
                        .orElse(null);
            }
        }
        Optional<Plafond> matchedPlafondOpt = Optional.ofNullable(resolvedPlafond);

        UUID matchedPlafondId = null;
        String matchedPlafondNama = null;
        BigDecimal matchedMinPendapatan = null;
        BigDecimal matchedPlafondMaksimal = null;
        BigDecimal estimasiPlafondDisetujui = BigDecimal.ZERO;

        if (matchedPlafondOpt.isPresent()) {
            Plafond p = matchedPlafondOpt.get();
            matchedPlafondId = p.getId();
            matchedPlafondNama = p.getNama();
            matchedMinPendapatan = p.getMinPendapatan();
            matchedPlafondMaksimal = p.getMaxPlafond() != null ? p.getMaxPlafond() : p.getPlafondMaksimal();

            PlafondCalculator.PersonalizedPlafondResult res = PlafondCalculator.calculate(
                    p, skor, pendapatan, totalCicilan,
                    scoring.getLamaBekerjaBulan() != null ? scoring.getLamaBekerjaBulan() : 0);
            estimasiPlafondDisetujui = res.finalApprovedPlafond();
        }


        // 3. Rekomendasi & Ringkasan Penilaian Sistem
        List<String> indikatorAmbigu = new ArrayList<>();
        boolean isAmbigu = false;

        String rekomendasiAksi = "PERTAHANKAN_TIER";
        UUID rekomendasiTierId = matchedPlafondId;
        String rekomendasiTierNama = matchedPlafondNama;
        BigDecimal rekomendasiBunga = matchedPlafondOpt.map(Plafond::getBunga).orElse(BigDecimal.valueOf(1.25));
        BigDecimal rekomendasiBiayaAdmin = matchedPlafondOpt.map(Plafond::getBiayaAdmin).orElse(BigDecimal.valueOf(250_000));
        String rekomendasiAlasan = "Data keuangan dan skor kredit telah dianalisis secara deterministik dengan Safe Capacity Cap.";

        String ringkasanAnalisis = "Hasil scoring konsisten dan deterministik. Plafon dihitung secara personalisasi proporsional.";

        String keputusanSistem = "TIDAK LAYAK";
        if (skor >= 75) {
            keputusanSistem = "LAYAK";
        } else if (skor >= 60) {
            keputusanSistem = "PERLU REVIEW";
        }

        ScoringBreakdown breakdown = ScoringBreakdown.builder()
                .dbrDetail(String.format("DBR: %.1f%% (Cicilan: Rp %s / Pendapatan: Rp %s)",
                        dbrPercentage, formatRupiah(totalCicilan), formatRupiah(pendapatan)))
                .pendapatanDetail(String.format("Pendapatan: Rp %s", formatRupiah(pendapatan)))
                .lamaBekerjaDetail(String.format("Lama Bekerja: %d Bulan", scoring.getLamaBekerjaBulan() != null ? scoring.getLamaBekerjaBulan() : 0))
                .statusPekerjaanDetail(String.format("Status Pekerjaan: %s", scoring.getStatusPekerjaan() != null ? scoring.getStatusPekerjaan() : "-"))
                .build();

        CustomerPlafondService.CustomerPlafondSummary plafondSummary = (scoring.getMstCustomerId() != null && customerPlafondService != null)
                ? customerPlafondService.calculatePlafondSummary(scoring.getMstCustomerId())
                : new CustomerPlafondService.CustomerPlafondSummary(estimasiPlafondDisetujui, BigDecimal.ZERO, estimasiPlafondDisetujui);

        BigDecimal finalTotalPlafond = (plafondSummary.totalPlafond() != null && plafondSummary.totalPlafond().compareTo(BigDecimal.ZERO) > 0)
                ? plafondSummary.totalPlafond()
                : estimasiPlafondDisetujui;
        BigDecimal finalAvailablePlafond = (plafondSummary.availablePlafond() != null && plafondSummary.totalPlafond().compareTo(BigDecimal.ZERO) > 0)
                ? plafondSummary.availablePlafond()
                : estimasiPlafondDisetujui;

        return ScoringAnalysisResponse.builder()
                .skor(skor)
                .statusScoring(statusScoring)
                .keputusanSistem(keputusanSistem)
                .dbr(dbr)
                .dbrPercentage(dbrPercentage)
                .matchedPlafondId(matchedPlafondId)
                .matchedPlafondNama(matchedPlafondNama)
                .matchedMinPendapatan(matchedMinPendapatan)
                .matchedPlafondMaksimal(matchedPlafondMaksimal)
                .estimasiPlafondDisetujui(estimasiPlafondDisetujui)
                .totalPlafond(finalTotalPlafond)
                .usedPlafond(plafondSummary.usedPlafond())
                .availablePlafond(finalAvailablePlafond)
                .isAmbigu(isAmbigu)
                .indikatorAmbigu(indikatorAmbigu)
                .ringkasanAnalisis(ringkasanAnalisis)
                .rekomendasiAksi(rekomendasiAksi)
                .rekomendasiTierId(rekomendasiTierId)
                .rekomendasiTierNama(rekomendasiTierNama)
                .rekomendasiBunga(rekomendasiBunga)
                .rekomendasiBiayaAdmin(rekomendasiBiayaAdmin)
                .rekomendasiAlasan(rekomendasiAlasan)
                .breakdown(breakdown)
                .build();
    }

    private String formatRupiah(BigDecimal amount) {
        if (amount == null) {
            return "0";
        }
        return NumberFormat.getIntegerInstance(Locale.of("id", "ID")).format(amount);
    }


    private double scoreDbr(BigDecimal dbr) {
        double value = dbr.doubleValue();
        if (value <= 0.30)
            return 100;
        if (value <= 0.40)
            return 75;
        if (value <= 0.50)
            return 50;
        if (value <= 0.60)
            return 25;
        return 0;
    }

    private double scorePendapatan(BigDecimal pendapatan) {
        double value = pendapatan.doubleValue();
        if (value >= 15_000_000)
            return 100;
        if (value >= 10_000_000)
            return 80;
        if (value >= 5_000_000)
            return 60;
        if (value >= 3_000_000)
            return 40;
        return 20;
    }

    private double scoreLamaBekerja(int bulan) {
        if (bulan >= 60)
            return 100;
        if (bulan >= 36)
            return 80;
        if (bulan >= 12)
            return 60;
        if (bulan >= 6)
            return 40;
        return 20;
    }

    private double scoreStatusPekerjaan(String status) {
        if (status == null || status.isBlank()) {
            return 30;
        }
        String normalized = status.trim().toUpperCase().replace(" ", "_").replace("-", "_");
        return switch (normalized) {
            case "PNS", "BUMN", "TNI_POLRI", "TNI", "POLRI", "KARYAWAN_TETAP", "PEGAWAI_TETAP", "TETAP" -> 100;
            case "KARYAWAN_KONTRAK", "PEGAWAI_KONTRAK", "KONTRAK", "PROFESIONAL" -> 70;
            case "WIRASWASTA", "PENGUSAHA", "ENTREPRENEUR", "PEDAGANG" -> 50;
            case "FREELANCE", "PEKERJA_LEPAS", "PENSIUNAN" -> 40;
            case "IBU_RUMAH_TANGGA", "LAINNYA" -> 30;
            default -> 30;
        };
    }





    public PlafondCalculator.PersonalizedPlafondResult calculatePersonalizedPlafond(
            Plafond plafond,
            int skor,
            BigDecimal pendapatan,
            BigDecimal totalCicilanLain,
            Integer lamaBekerjaBulan) {
        return PlafondCalculator.calculate(plafond, skor, pendapatan, totalCicilanLain, lamaBekerjaBulan);
    }

    private String mapSkorToKeputusan(double skor) {
        if (skor >= 75)
            return "APPROVED";
        if (skor >= 60)
            return "REVIEW";
        return "REJECTED";
    }

    public record ScoringResult(double score, String decision) {
    }
}

