package com.bcafinance.backend_saku.service;

import java.math.BigDecimal;
import org.springframework.stereotype.Service;

@Service
public class ScoringService {

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
