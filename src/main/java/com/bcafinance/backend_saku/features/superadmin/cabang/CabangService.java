package com.bcafinance.backend_saku.features.superadmin.cabang;

import com.bcafinance.backend_saku.core.entity.Cabang;
import com.bcafinance.backend_saku.core.exception.BussinessRuleException;
import com.bcafinance.backend_saku.core.repository.CabangRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CabangService {

    private final CabangRepository cabangRepository;

    @Transactional
    public CabangResponse create(CabangRequest request) {
        Cabang cabang = new Cabang();
        cabang.setId(UUID.randomUUID());
        cabang.setCreatedDate(LocalDateTime.now());
        applyRequest(cabang, request);
        return toResponse(cabangRepository.save(cabang));
    }

    public List<CabangResponse> findAll() {
        return cabangRepository.findAll().stream().map(this::toResponse).toList();
    }

    public List<CabangResponse> findAllActive() {
        return cabangRepository.findAllByStatusTrueOrderByNamaAsc().stream()
                .map(this::toResponse)
                .toList();
    }

    public CabangResponse findById(UUID id) {
        return toResponse(getCabang(id));
    }

    @Transactional
    public CabangResponse update(UUID id, CabangRequest request) {
        Cabang cabang = getCabang(id);
        applyRequest(cabang, request);
        return toResponse(cabangRepository.save(cabang));
    }

    @Transactional
    public void delete(UUID id) {
        Cabang cabang = getCabang(id);
        cabangRepository.delete(cabang);
    }



    private void applyRequest(Cabang cabang, CabangRequest request) {
        cabang.setNama(request.getNama());
        cabang.setKota(request.getKota());
        cabang.setIsDefault(request.getIsDefault());
        cabang.setStatus(request.getStatus());
        cabang.setUpdatedDate(LocalDateTime.now());
    }

    private Cabang getCabang(UUID id) {
        return cabangRepository.findById(id)
                .orElseThrow(() -> new BussinessRuleException("Cabang tidak ditemukan"));
    }

    private CabangResponse toResponse(Cabang cabang) {
        CabangResponse response = new CabangResponse();
        response.setId(cabang.getId());
        response.setNama(cabang.getNama());
        response.setKota(cabang.getKota());
        response.setIsDefault(cabang.getIsDefault());
        response.setStatus(cabang.getStatus());
        return response;
    }
}
