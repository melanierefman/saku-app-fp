package com.bcafinance.backend_saku.features.public_api.service;

import com.bcafinance.backend_saku.core.entity.Plafond;

import com.bcafinance.backend_saku.core.repository.PlafondRepository;
import com.bcafinance.backend_saku.features.public_api.dto.PublicPlafondResponse;
import com.bcafinance.backend_saku.features.public_api.dto.SimulasiPinjamanResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PublicService {

    private final PlafondRepository plafondRepository;

    @Cacheable(value = "publicPlafonds")
    @Transactional(readOnly = true)
    public List<PublicPlafondResponse> getPublicPlafonds() {
        return plafondRepository.findAllByStatusTrue().stream()
                .map(this::toPlafondResponse)
                .toList();
    }

    @Cacheable(value = "simulasiPinjaman", key = "#jumlahPinjaman + '-' + #tenorBulan")
    @Transactional(readOnly = true)
    public SimulasiPinjamanResponse hitungSimulasi(BigDecimal jumlahPinjaman, Integer tenorBulan) {

        if (jumlahPinjaman == null || jumlahPinjaman.compareTo(BigDecimal.ZERO) <= 0) {
            jumlahPinjaman = BigDecimal.valueOf(5_000_000);
        }
        if (tenorBulan == null || tenorBulan <= 0) {
            tenorBulan = 12;
        }

        // Cari plafond yang cocok dengan nominal pinjaman
        List<Plafond> activePlafonds = plafondRepository.findAllByStatusTrue();
        final BigDecimal targetPinjaman = jumlahPinjaman;

        Optional<Plafond> matchedPlafond = activePlafonds.stream()
                .filter(p -> p.getMinPlafond() != null && p.getMaxPlafond() != null
                        && targetPinjaman.compareTo(p.getMinPlafond()) >= 0
                        && targetPinjaman.compareTo(p.getMaxPlafond()) <= 0)
                .findFirst();

        BigDecimal sukuBungaPersen = BigDecimal.valueOf(1.5); // default 1.5% per bulan
        BigDecimal biayaAdmin = BigDecimal.valueOf(50_000); // default
        String tierName = "Reguler";

        if (matchedPlafond.isPresent()) {
            Plafond p = matchedPlafond.get();
            if (p.getBunga() != null) {
                sukuBungaPersen = p.getBunga();
            }
            if (p.getBiayaAdmin() != null) {
                biayaAdmin = p.getBiayaAdmin();
            }
            tierName = p.getNama();
        } else if (!activePlafonds.isEmpty()) {
            Plafond first = activePlafonds.get(0);
            if (first.getBunga() != null) {
                sukuBungaPersen = first.getBunga();
            }
            if (first.getBiayaAdmin() != null) {
                biayaAdmin = first.getBiayaAdmin();
            }
            tierName = first.getNama();
        }

        BigDecimal ratePct = (sukuBungaPersen.compareTo(BigDecimal.ONE) <= 0 && sukuBungaPersen.compareTo(BigDecimal.ZERO) > 0)
                ? sukuBungaPersen.multiply(BigDecimal.valueOf(100))
                : sukuBungaPersen;

        BigDecimal tenorBD = BigDecimal.valueOf(tenorBulan);
        BigDecimal cicilanPokokBulanan = jumlahPinjaman.divide(tenorBD, 2, RoundingMode.HALF_UP);
        BigDecimal bungaBulanan = jumlahPinjaman.multiply(ratePct)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal totalCicilanBulanan = cicilanPokokBulanan.add(bungaBulanan);
        BigDecimal totalPembayaran = totalCicilanBulanan.multiply(tenorBD).add(biayaAdmin);

        return SimulasiPinjamanResponse.builder()
                .jumlahPinjaman(jumlahPinjaman)
                .tenorBulan(tenorBulan)
                .sukuBungaPersen(ratePct)
                .cicilanPokokBulanan(cicilanPokokBulanan)
                .bungaBulanan(bungaBulanan)
                .totalCicilanBulanan(totalCicilanBulanan)
                .biayaAdmin(biayaAdmin)
                .totalPembayaran(totalPembayaran)
                .estimasiTierPlafond(tierName)
                .build();
    }

    private PublicPlafondResponse toPlafondResponse(Plafond p) {
        BigDecimal rawBunga = p.getBunga();
        BigDecimal normalizedBunga = (rawBunga != null && rawBunga.compareTo(BigDecimal.ONE) <= 0 && rawBunga.compareTo(BigDecimal.ZERO) > 0)
                ? rawBunga.multiply(BigDecimal.valueOf(100))
                : rawBunga;

        return PublicPlafondResponse.builder()
                .id(p.getId())
                .nama(p.getNama())
                .minPlafond(p.getMinPlafond())
                .maxPlafond(p.getMaxPlafond())
                .bunga(normalizedBunga)
                .biayaAdmin(p.getBiayaAdmin())
                .minSkor(p.getMinSkor())
                .maxSkor(p.getMaxSkor())
                .build();
    }
}
