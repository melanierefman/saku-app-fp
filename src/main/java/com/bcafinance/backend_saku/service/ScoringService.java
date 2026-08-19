package com.bcafinance.backend_saku.service;

import com.bcafinance.backend_saku.dto.ScoringAnalysisResponse;
import com.bcafinance.backend_saku.dto.ScoringBreakdown;
import com.bcafinance.backend_saku.entity.Plafond;
import com.bcafinance.backend_saku.entity.ScoringCustomer;
import com.bcafinance.backend_saku.repository.PlafondRepository;
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

    public ScoringResult calculateScore(
            BigDecimal totalCicilanLainnya,
            BigDecimal pendapatan,
            int lamaBekerjaBulan,
            String statusPekerjaan,
            int lamaJadiNasabahBulan) {
        BigDecimal dbr = totalCicilanLainnya.divide(pendapatan, 6, java.math.RoundingMode.HALF_UP);

        double skorAkhir = (scoreDbr(dbr) * 0.35)
                + (scorePendapatan(pendapatan) * 0.20)
                + (scoreLamaBekerja(lamaBekerjaBulan) * 0.20)
                + (scoreStatusPekerjaan(statusPekerjaan) * 0.15)
                + (scoreLamaNasabah(lamaJadiNasabahBulan) * 0.10);

        return new ScoringResult(skorAkhir, mapSkorToKeputusan(skorAkhir));
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

        // 2. Evaluasi Plafond
        List<Plafond> activePlafonds = plafondRepository.findAllByStatusTrue();
        Optional<Plafond> matchedPlafondOpt = plafondRepository
                .findTopByMinPendapatanLessThanEqualAndStatusTrueOrderByMinPendapatanDesc(pendapatan);

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
            matchedPlafondMaksimal = p.getPlafondMaksimal();

            int percentage = skor >= 75 ? 100 : (skor >= 60 ? 70 : 0);
            if (percentage > 0 && matchedPlafondMaksimal != null) {
                estimasiPlafondDisetujui = matchedPlafondMaksimal
                        .multiply(BigDecimal.valueOf(percentage).movePointLeft(2))
                        .setScale(2, RoundingMode.HALF_UP);
            }
        }

        // 3. Deteksi Kondisi Ambigu / Anomali Scoring (untuk Marketing & BM)
        List<String> indikatorAmbigu = new ArrayList<>();
        boolean isAmbigu = false;

        // a. Cek apakah ada Plafond yang tier skornya cocok dengan skor customer tetapi pendapatannya di bawah syarat
        Optional<Plafond> tierBySkor = activePlafonds.stream()
                .filter(p -> p.getMinSkor() != null && p.getMaxSkor() != null &&
                        skor >= p.getMinSkor() && skor <= p.getMaxSkor())
                .findFirst();

        if (tierBySkor.isPresent()) {
            Plafond pTier = tierBySkor.get();
            if (pTier.getMinPendapatan() != null && pendapatan.compareTo(pTier.getMinPendapatan()) < 0) {
                isAmbigu = true;
                indikatorAmbigu.add(String.format(
                        "Skor kredit (%d) masuk range %s (rentang skor %d-%d), namun pendapatan bulanan (Rp %s) di bawah syarat minimum pendapatan %s (Rp %s).",
                        skor, pTier.getNama(), pTier.getMinSkor(), pTier.getMaxSkor(),
                        formatRupiah(pendapatan), pTier.getNama(), formatRupiah(pTier.getMinPendapatan())));
            }
        }

        // b. Cek apakah customer tidak memenuhi plafond manapun berdasarkan pendapatan
        if (matchedPlafondOpt.isEmpty() && !activePlafonds.isEmpty()) {
            isAmbigu = true;
            indikatorAmbigu.add(String.format(
                    "Pendapatan bulanan (Rp %s) belum memenuhi batas minimum pendapatan dari seluruh produk plafond aktif.",
                    formatRupiah(pendapatan)));
        } else if (matchedPlafondOpt.isPresent()) {
            Plafond p = matchedPlafondOpt.get();
            if (p.getMinSkor() != null && skor < p.getMinSkor()) {
                isAmbigu = true;
                indikatorAmbigu.add(String.format(
                        "Pendapatan memenuhi syarat tier %s, namun skor kredit (%d) berada di bawah standar minimum skor tier ini (%d).",
                        p.getNama(), skor, p.getMinSkor()));
            }
        }

        // c. Cek DBR tinggi (di atas 40%)
        if (dbr.compareTo(BigDecimal.valueOf(0.40)) > 0) {
            isAmbigu = true;
            indikatorAmbigu.add(String.format(
                    "Debt Burden Ratio (DBR) tergolong tinggi yaitu %.1f%% (cicilan bulanan Rp %s dari pendapatan Rp %s).",
                    dbrPercentage, formatRupiah(totalCicilan), formatRupiah(pendapatan)));
        }

        // d. Cek status scoring REVIEW (zona abu-abu skor 60 - 74)
        if ("REVIEW".equalsIgnoreCase(statusScoring) || (skor >= 60 && skor < 75)) {
            isAmbigu = true;
            indikatorAmbigu.add(String.format(
                    "Skor kredit (%d) berada dalam kategori REVIEW (60 - 74), membutuhkan verifikasi manual oleh Marketing/BM.",
                    skor));
        }

        // e. Cek masa kerja & status pekerjaan
        if (scoring.getLamaBekerjaBulan() != null && scoring.getLamaBekerjaBulan() < 12) {
            indikatorAmbigu.add(String.format(
                    "Masa kerja baru berjalan %d bulan (< 1 tahun), memiliki potensi risiko stabilitas penghasilan.",
                    scoring.getLamaBekerjaBulan()));
        }

        String ringkasanAnalisis;
        if (isAmbigu) {
            ringkasanAnalisis = "Sistem mendeteksi adanya inkonsistensi/faktor ambigu antara skor kredit, pendapatan, atau rasio cicilan. Disarankan Marketing / BM memeriksa kelengkapan dokumen dan kemampuan bayar.";
        } else {
            indikatorAmbigu.add("Data keuangan dan skor kredit konsisten dengan kriteria tier plafond sistem.");
            ringkasanAnalisis = "Hasil scoring konsisten. Customer memenuhi seluruh kriteria kelayakan sistem.";
        }

        String keputusanSistem = "TIDAK LAYAK (REJECTED)";
        if (skor >= 75) {
            keputusanSistem = "LAYAK (APPROVED)";
        } else if (skor >= 60) {
            keputusanSistem = "PERLU REVIEW (REVIEW)";
        }

        ScoringBreakdown breakdown = ScoringBreakdown.builder()
                .dbrDetail(String.format("DBR: %.1f%% (Cicilan: Rp %s / Pendapatan: Rp %s)",
                        dbrPercentage, formatRupiah(totalCicilan), formatRupiah(pendapatan)))
                .pendapatanDetail(String.format("Pendapatan: Rp %s", formatRupiah(pendapatan)))
                .lamaBekerjaDetail(String.format("Lama Bekerja: %d Bulan", scoring.getLamaBekerjaBulan() != null ? scoring.getLamaBekerjaBulan() : 0))
                .statusPekerjaanDetail(String.format("Status Pekerjaan: %s", scoring.getStatusPekerjaan() != null ? scoring.getStatusPekerjaan() : "-"))
                .lamaNasabahDetail(String.format("Lama Jadi Nasabah: %d Bulan", scoring.getLamaJadiNasabahBulan() != null ? scoring.getLamaJadiNasabahBulan() : 0))
                .build();

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
                .isAmbigu(isAmbigu)
                .indikatorAmbigu(indikatorAmbigu)
                .ringkasanAnalisis(ringkasanAnalisis)
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
        return switch (status.trim().toUpperCase()) {
            case "KARYAWAN_TETAP" -> 100;
            case "KARYAWAN_KONTRAK", "PROFESIONAL" -> 70;
            case "WIRASWASTA" -> 50;
            default -> 30;
        };
    }

    private double scoreLamaNasabah(int bulan) {
        if (bulan >= 24)
            return 100;
        if (bulan >= 12)
            return 70;
        if (bulan >= 1)
            return 40;
        return 20;
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

