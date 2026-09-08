package com.bcafinance.backend_saku.features.customer.service;

import com.bcafinance.backend_saku.core.entity.Angsuran;
import com.bcafinance.backend_saku.core.entity.PengajuanPinjaman;
import com.bcafinance.backend_saku.core.entity.Plafond;
import com.bcafinance.backend_saku.core.entity.ScoringCustomer;
import com.bcafinance.backend_saku.core.repository.AngsuranRepository;
import com.bcafinance.backend_saku.core.repository.PengajuanPinjamanRepository;
import com.bcafinance.backend_saku.core.repository.PlafondRepository;
import com.bcafinance.backend_saku.core.repository.ScoringCustomerRepository;
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
            BigDecimal availablePlafond
    ) {}

    /**
     * Menghitung ringkasan plafond nasabah secara real-time:
     * Available Plafond = max(0, Total Plafond - Used Plafond)
     */
    public CustomerPlafondSummary calculatePlafondSummary(UUID customerId) {
        if (customerId == null) {
            return new CustomerPlafondSummary(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        }

        Optional<ScoringCustomer> scoringOpt = scoringRepository.findFirstByMstCustomerIdOrderByCreatedDateDesc(customerId);
        if (scoringOpt.isEmpty()) {
            return new CustomerPlafondSummary(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        }

        ScoringCustomer scoring = scoringOpt.get();
        BigDecimal totalLimit = resolveTotalPlafond(scoring);
        BigDecimal usedLimit = calculateUsedPlafond(customerId);
        BigDecimal availableLimit = totalLimit.subtract(usedLimit);
        if (availableLimit.compareTo(BigDecimal.ZERO) < 0) {
            availableLimit = BigDecimal.ZERO;
        }

        return new CustomerPlafondSummary(
                totalLimit.setScale(2, RoundingMode.HALF_UP),
                usedLimit.setScale(2, RoundingMode.HALF_UP),
                availableLimit.setScale(2, RoundingMode.HALF_UP)
        );
    }

    /**
     * Menentukan Total Plafond maksimal yang disetujui berdasarkan scoring nasabah
     */
    public BigDecimal resolveTotalPlafond(ScoringCustomer scoring) {
        if (scoring == null) {
            return BigDecimal.ZERO;
        }

        Plafond plafond = null;
        if (scoring.getMstPlafondId() != null) {
            plafond = plafondRepository.findById(scoring.getMstPlafondId()).orElse(null);
        }

        if (plafond == null) {
            BigDecimal pendapatan = scoring.getPenghasilanBulanan() != null ? scoring.getPenghasilanBulanan() : BigDecimal.ZERO;
            plafond = plafondRepository
                    .findTopByMinPendapatanLessThanEqualAndStatusTrueOrderByMinPendapatanDesc(pendapatan)
                    .or(() -> plafondRepository.findFirstByStatusTrueOrderByMinSkorAsc())
                    .orElse(null);
        }

        if (plafond == null) {
            return BigDecimal.valueOf(50_000_000);
        }

        int skor = scoring.getSkor() != null ? scoring.getSkor() : 0;
        int percentage = skor >= 75 ? 100 : (skor >= 60 ? 70 : 50);

        if (plafond.getPlafondMaksimal() != null) {
            return plafond.getPlafondMaksimal()
                    .multiply(BigDecimal.valueOf(percentage).movePointLeft(2))
                    .setScale(2, RoundingMode.HALF_UP);
        } else if (plafond.getMaxPlafond() != null) {
            return plafond.getMaxPlafond()
                    .multiply(BigDecimal.valueOf(percentage).movePointLeft(2))
                    .setScale(2, RoundingMode.HALF_UP);
        } else if (plafond.getMinPlafond() != null) {
            return plafond.getMinPlafond();
        }

        return BigDecimal.valueOf(50_000_000);
    }

    /**
     * Menghitung total pokok pinjaman yang sedang terpakai / terikat (Outstanding Principal)
     */
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
                // Mereservasi limit sebesar pokok pinjaman
                if (p.getJumlahPinjaman() != null) {
                    totalUsed = totalUsed.add(p.getJumlahPinjaman());
                }
            }
        }

        return totalUsed.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Menghitung sisa pokok untuk pinjaman aktif berdasarkan jadwal angsuran yang lunas
     */
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

    public static String formatRupiah(BigDecimal amount) {
        if (amount == null) {
            return "0";
        }
        return NumberFormat.getIntegerInstance(Locale.of("id", "ID")).format(amount);
    }
}
