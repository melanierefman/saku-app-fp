package com.bcafinance.backend_saku.features.scoring.service;

import com.bcafinance.backend_saku.core.entity.Plafond;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Engine Kalkulasi Plafon Personalisasi SAKU (Solusi Aman Keuangan Untukmu)
 * Mengintegrasikan 4 metode:
 * 1. Weighted Sum Model (WSM) untuk pembobotan kriteria
 * 2. Min-Max Normalization untuk faktor posisi skor di dalam tier
 * 3. Linear Mapping & Clamping untuk penentuan limit nominal riil
 * 4. Debt Service Ratio (DSR / Prudential Lending) untuk Safe Capacity Cap
 */
public class PlafondCalculator {

    public record PersonalizedPlafondResult(
            BigDecimal basePlafond,
            BigDecimal safeCapacityCap,
            BigDecimal finalApprovedPlafond,
            double factorScore,
            double factorDbr,
            double factorTenure,
            double totalWeight,
            String breakdownDetail
    ) {}

    public static final BigDecimal MIN_STEP_ROUNDING = BigDecimal.valueOf(500_000);

    public static PersonalizedPlafondResult calculate(
            Plafond plafond,
            int skor,
            BigDecimal pendapatan,
            BigDecimal totalCicilanLain,
            Integer lamaBekerjaBulan) {

        BigDecimal income = pendapatan != null ? pendapatan : BigDecimal.ZERO;
        BigDecimal cicilan = totalCicilanLain != null ? totalCicilanLain : BigDecimal.ZERO;
        int tenure = lamaBekerjaBulan != null ? lamaBekerjaBulan : 0;

        BigDecimal minPlafond = (plafond != null && plafond.getMinPlafond() != null)
                ? plafond.getMinPlafond()
                : BigDecimal.valueOf(1_000_000);
        BigDecimal maxPlafond = (plafond != null && plafond.getMaxPlafond() != null)
                ? plafond.getMaxPlafond()
                : (plafond != null && plafond.getPlafondMaksimal() != null
                        ? plafond.getPlafondMaksimal()
                        : minPlafond);

        if (maxPlafond.compareTo(minPlafond) < 0) {
            maxPlafond = minPlafond;
        }

        int minSkor = (plafond != null && plafond.getMinSkor() != null) ? plafond.getMinSkor() : 60;
        int maxSkor = (plafond != null && plafond.getMaxSkor() != null) ? plafond.getMaxSkor() : 74;

        // 1. Min-Max Normalization: Posisi Skor Kredit di dalam rentang Tier [0.0 - 1.0]
        double factorScore;
        if (maxSkor > minSkor) {
            factorScore = ((double) skor - minSkor) / ((double) maxSkor - minSkor);
            factorScore = Math.max(0.0, Math.min(1.0, factorScore));
        } else {
            factorScore = 1.0;
        }

        // 2. Faktor Kesehatan Beban Cicilan (DBR)
        double dbrRatio = (income.compareTo(BigDecimal.ZERO) > 0)
                ? cicilan.divide(income, 4, RoundingMode.HALF_UP).doubleValue()
                : 0.0;
        double factorDbr;
        if (dbrRatio <= 0.15) {
            factorDbr = 1.00;
        } else if (dbrRatio <= 0.30) {
            factorDbr = 0.85;
        } else if (dbrRatio <= 0.40) {
            factorDbr = 0.70;
        } else if (dbrRatio <= 0.50) {
            factorDbr = 0.50;
        } else {
            factorDbr = 0.30;
        }

        // 3. Faktor Stabilitas Masa Kerja (Tenure)
        double factorTenure;
        if (tenure >= 36) {
            factorTenure = 1.00;
        } else if (tenure >= 12) {
            factorTenure = 0.85;
        } else {
            factorTenure = 0.70;
        }

        // 4. Weighted Sum Model (WSM): Bobot Gabungan Penentuan Limit
        double totalWeight = (0.50 * factorScore) + (0.35 * factorDbr) + (0.15 * factorTenure);
        totalWeight = Math.max(0.0, Math.min(1.0, totalWeight));

        // 5. Linear Mapping: Menghitung Plafon Dasar (Base Plafond)
        BigDecimal range = maxPlafond.subtract(minPlafond);
        BigDecimal additionalPlafond = range.multiply(BigDecimal.valueOf(totalWeight));
        BigDecimal basePlafond = minPlafond.add(additionalPlafond).setScale(2, RoundingMode.HALF_UP);

        // 6. Safe Capacity Cap (Prudential Lending 35% x Sisa Gaji Bebas x 12 Bulan)
        BigDecimal disposableIncome = income.subtract(cicilan);
        if (disposableIncome.compareTo(BigDecimal.ZERO) < 0) {
            disposableIncome = BigDecimal.ZERO;
        }
        BigDecimal maxMonthlySafeInstallment = disposableIncome.multiply(BigDecimal.valueOf(0.35));
        BigDecimal safeCapacityCap = maxMonthlySafeInstallment.multiply(BigDecimal.valueOf(12))
                .setScale(2, RoundingMode.HALF_UP);

        // 7. Clamping & Pembulatan Rapi ke Kelipatan Rp 500.000
        BigDecimal candidate = (safeCapacityCap.compareTo(BigDecimal.ZERO) > 0)
                ? basePlafond.min(safeCapacityCap)
                : basePlafond;

        // Pastikan berada di dalam koridor rentang tier [minPlafond, maxPlafond]
        BigDecimal clamped = candidate.max(minPlafond).min(maxPlafond);

        // Pembulatan ke bawah (floor) ke kelipatan Rp 500.000
        long step = MIN_STEP_ROUNDING.longValue();
        long clampedVal = clamped.longValue();
        long roundedVal = (clampedVal / step) * step;
        if (roundedVal < minPlafond.longValue()) {
            roundedVal = minPlafond.longValue();
        }
        BigDecimal finalApprovedPlafond = BigDecimal.valueOf(roundedVal).setScale(2, RoundingMode.HALF_UP);

        String breakdownDetail = String.format(
                "Faktor Skor: %.3f (50%%), Faktor DBR: %.2f (35%%), Faktor Masa Kerja: %.2f (15%%) -> Bobot WSM: %.3f | Base: Rp %s | Safe Cap: Rp %s | Disetujui: Rp %s",
                factorScore, factorDbr, factorTenure, totalWeight,
                formatRupiah(basePlafond), formatRupiah(safeCapacityCap), formatRupiah(finalApprovedPlafond));

        return new PersonalizedPlafondResult(
                basePlafond,
                safeCapacityCap,
                finalApprovedPlafond,
                factorScore,
                factorDbr,
                factorTenure,
                totalWeight,
                breakdownDetail
        );
    }

    private static String formatRupiah(BigDecimal amount) {
        if (amount == null) return "0";
        return NumberFormat.getIntegerInstance(Locale.of("id", "ID")).format(amount);
    }
}
