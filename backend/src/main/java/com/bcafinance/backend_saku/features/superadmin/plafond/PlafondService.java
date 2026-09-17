package com.bcafinance.backend_saku.features.superadmin.plafond;

import com.bcafinance.backend_saku.core.entity.Plafond;
import com.bcafinance.backend_saku.core.exception.BussinessRuleException;
import com.bcafinance.backend_saku.core.repository.PlafondRepository;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlafondService {

    private final PlafondRepository plafondRepository;

    @Transactional
    @CacheEvict(value = "plafondList", allEntries = true)
    public PlafondResponse create(PlafondRequest request) {
        Plafond plafond = new Plafond();
        plafond.setId(UUID.randomUUID());
        plafond.setCreatedDate(LocalDateTime.now());
        applyRequest(plafond, request);
        return toResponse(plafondRepository.save(plafond));
    }

    @Cacheable(value = "plafondList", key = "'all'")
    public List<PlafondResponse> findAll() {
        return plafondRepository.findAll().stream().map(this::toResponse).toList();
    }

    public PlafondResponse findById(UUID id) {
        return toResponse(getPlafond(id));
    }

    @Transactional
    @CacheEvict(value = "plafondList", allEntries = true)
    public PlafondResponse update(UUID id, PlafondRequest request) {
        Plafond plafond = getPlafond(id);
        applyRequest(plafond, request);
        return toResponse(plafondRepository.save(plafond));
    }

    @Transactional
    @CacheEvict(value = "plafondList", allEntries = true)
    public void delete(UUID id) {
        plafondRepository.delete(getPlafond(id));
    }



    public PlafondCalculationResponse calculateApprovedAmount(BigDecimal pendapatan, double skorAkhir) {
        List<Plafond> activePlafonds = plafondRepository.findAllByStatusTrue();
        int skorInt = (int) Math.round(skorAkhir);
        BigDecimal income = pendapatan != null ? pendapatan : BigDecimal.ZERO;

        // 1. Cari tier berdasarkan rentang skor kredit
        Optional<Plafond> matchedByScore = activePlafonds.stream()
                .filter(p -> p.getMinSkor() != null && p.getMaxSkor() != null
                        && skorInt >= p.getMinSkor() && skorInt <= p.getMaxSkor())
                .findFirst();

        Plafond plafond = null;
        if (matchedByScore.isPresent()) {
            Plafond pScore = matchedByScore.get();
            BigDecimal minIncome = pScore.getMinPendapatan() != null ? pScore.getMinPendapatan() : BigDecimal.ZERO;

            // Hard Rule: Jika pendapatan nasabah < syarat minimal tier skornya, sistem otomatis down-tier ke tier tertinggi yang sesuai gajinya
            if (income.compareTo(minIncome) < 0) {
                plafond = plafondRepository
                        .findTopByMinPendapatanLessThanEqualAndStatusTrueOrderByMinPendapatanDesc(income)
                        .orElse(pScore);
            } else {
                plafond = pScore;
            }
        }

        if (plafond == null) {
            plafond = plafondRepository
                    .findTopByMinPendapatanLessThanEqualAndStatusTrueOrderByMinPendapatanDesc(income)
                    .or(() -> plafondRepository.findFirstByStatusTrueOrderByMinSkorAsc())
                    .orElseThrow(() -> new BussinessRuleException(
                            "Data master plafond aktif belum tersedia di sistem"));
        }

        int percentage = mapScoreToPercentage(skorAkhir);
        if (percentage == 0) {
            percentage = 50;
        }
        String decision = skorAkhir >= 75 ? "APPROVED" : skorAkhir >= 60 ? "REVIEW" : "REJECTED";
        BigDecimal approvedAmount = plafond.getPlafondMaksimal() != null
                ? plafond.getPlafondMaksimal()
                        .multiply(BigDecimal.valueOf(percentage).movePointLeft(2))
                        .setScale(2, RoundingMode.HALF_UP)
                : (plafond.getMinPlafond() != null ? plafond.getMinPlafond() : BigDecimal.valueOf(1_000_000));

        return new PlafondCalculationResponse(
                plafond.getId(), plafond.getNama(), decision, percentage,
                plafond.getPlafondMaksimal(), approvedAmount);
    }


    private int mapScoreToPercentage(double score) {
        if (score >= 75)
            return 100;
        if (score >= 60)
            return 70;
        return 0;
    }

    private void applyRequest(Plafond plafond, PlafondRequest request) {
        if (request.getMinPlafond().compareTo(request.getMaxPlafond()) > 0) {
            throw new BussinessRuleException("Minimum plafond tidak boleh lebih besar dari maksimum plafond");
        }
        if (request.getMinSkor() > request.getMaxSkor()) {
            throw new BussinessRuleException("Minimum skor tidak boleh lebih besar dari maksimum skor");
        }

        plafond.setNama(request.getNama());
        plafond.setMinPendapatan(request.getMinPendapatan());
        plafond.setPlafondMaksimal(request.getPlafondMaksimal());
        plafond.setMinPlafond(request.getMinPlafond());
        plafond.setMaxPlafond(request.getMaxPlafond());
        plafond.setMinSkor(request.getMinSkor());
        plafond.setMaxSkor(request.getMaxSkor());
        plafond.setBunga(request.getBunga());
        plafond.setBiayaAdmin(request.getBiayaAdmin());
        plafond.setStatus(request.getStatus());
        plafond.setUpdatedDate(LocalDateTime.now());
    }

    private Plafond getPlafond(UUID id) {
        return plafondRepository.findById(id)
                .orElseThrow(() -> new BussinessRuleException("Plafond tidak ditemukan"));
    }

    private PlafondResponse toResponse(Plafond plafond) {
        PlafondResponse response = new PlafondResponse();
        response.setId(plafond.getId());
        response.setNama(plafond.getNama());
        response.setMinPendapatan(plafond.getMinPendapatan());
        response.setPlafondMaksimal(plafond.getPlafondMaksimal());
        response.setMinPlafond(plafond.getMinPlafond());
        response.setMaxPlafond(plafond.getMaxPlafond());
        response.setMinSkor(plafond.getMinSkor());
        response.setMaxSkor(plafond.getMaxSkor());
        response.setBunga(plafond.getBunga());
        response.setBiayaAdmin(plafond.getBiayaAdmin());
        response.setStatus(plafond.getStatus());
        response.setCreatedDate(plafond.getCreatedDate());
        response.setUpdatedDate(plafond.getUpdatedDate());
        return response;
    }
}
