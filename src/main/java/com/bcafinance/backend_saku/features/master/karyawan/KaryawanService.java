package com.bcafinance.backend_saku.features.master.karyawan;

import com.bcafinance.backend_saku.core.dto.PageResponse;
import com.bcafinance.backend_saku.core.entity.Cabang;
import com.bcafinance.backend_saku.core.entity.Karyawan;
import com.bcafinance.backend_saku.core.entity.Role;
import com.bcafinance.backend_saku.core.exception.BussinessRuleException;
import com.bcafinance.backend_saku.core.repository.CabangRepository;
import com.bcafinance.backend_saku.core.repository.KaryawanRepository;
import com.bcafinance.backend_saku.core.repository.RoleRepository;
import jakarta.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KaryawanService {

    private final KaryawanRepository karyawanRepository;
    private final CabangRepository cabangRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public KaryawanResponse create(KaryawanRequest request) {
        validateUnique(request.getEmail(), request.getUsername(), null);

        Karyawan karyawan = new Karyawan();
        karyawan.setId(UUID.randomUUID());
        karyawan.setCreatedDate(new Date());
        applyRequest(karyawan, request, true);

        return toResponse(karyawanRepository.save(karyawan));
    }

    public PageResponse<KaryawanResponse> findAllPaginated(int page, int size, String search, UUID roleId, UUID branchId, Boolean status) {
        if (page < 0) page = 0;
        if (size <= 0) size = 10;

        Pageable pageable = PageRequest.of(page, size);
        String searchTrimmed = (search != null && !search.trim().isEmpty()) ? search.trim() : null;

        Page<Karyawan> pageResult = karyawanRepository.findByFilters(searchTrimmed, roleId, branchId, status, pageable);
        return PageResponse.of(pageResult, this::toResponse);
    }

    public List<KaryawanResponse> findAll() {
        return karyawanRepository.findAll().stream().map(this::toResponse).toList();
    }

    public KaryawanResponse findById(UUID id) {
        return toResponse(getKaryawan(id));
    }

    @Transactional
    public KaryawanResponse update(UUID id, KaryawanRequest request) {
        Karyawan karyawan = getKaryawan(id);
        validateUnique(request.getEmail(), request.getUsername(), id);
        applyRequest(karyawan, request, false);
        return toResponse(karyawanRepository.save(karyawan));
    }

    @Transactional
    public void delete(UUID id) {
        Karyawan karyawan = getKaryawan(id);
        karyawanRepository.delete(karyawan);
    }

    private void applyRequest(Karyawan karyawan, KaryawanRequest request, boolean newPassword) {
        Role role = roleRepository.findById(request.getMstRoleId())
                .orElseThrow(() -> new BussinessRuleException("Role tidak ditemukan"));
        Cabang cabang = cabangRepository.findById(request.getMstBranchId())
                .orElseThrow(() -> new BussinessRuleException("Cabang tidak ditemukan"));

        if (!role.getStatus()) {
            throw new BussinessRuleException("Role tidak aktif");
        }

        karyawan.setNama(request.getNama());
        karyawan.setEmail(request.getEmail());
        karyawan.setUsername(request.getUsername());
        karyawan.setStatus(request.getStatus());
        karyawan.setRole(role);
        karyawan.setCabang(cabang);
        karyawan.setUpdatedDate(new Date());

        if (newPassword || request.getPassword() != null && !request.getPassword().isBlank()) {
            karyawan.setPassword(passwordEncoder.encode(request.getPassword()));
        }
    }

    private void validateUnique(String email, String username, UUID excludedId) {
        if (karyawanRepository.existsByEmail(email)
                && (excludedId == null || !karyawanRepository.findById(excludedId)
                        .map(karyawan -> email.equals(karyawan.getEmail())).orElse(false))) {
            throw new BussinessRuleException("Email karyawan sudah terdaftar");
        }
        if (karyawanRepository.existsByUsername(username)
                && (excludedId == null || !karyawanRepository.findById(excludedId)
                        .map(karyawan -> username.equals(karyawan.getUsername())).orElse(false))) {
            throw new BussinessRuleException("Username karyawan sudah terdaftar");
        }
    }

    private Karyawan getKaryawan(UUID id) {
        return karyawanRepository.findById(id)
                .orElseThrow(() -> new BussinessRuleException("Karyawan tidak ditemukan"));
    }

    private KaryawanResponse toResponse(Karyawan karyawan) {
        KaryawanResponse response = new KaryawanResponse();
        response.setId(karyawan.getId());
        response.setNama(karyawan.getNama());
        response.setEmail(karyawan.getEmail());
        response.setUsername(karyawan.getUsername());
        response.setStatus(karyawan.getStatus());
        response.setCreatedDate(karyawan.getCreatedDate());
        response.setUpdatedDate(karyawan.getUpdatedDate());

        if (karyawan.getRole() != null) {
            response.setMstRoleId(karyawan.getRole().getId());
            response.setRoleNama(karyawan.getRole().getNama());
        }
        if (karyawan.getCabang() != null) {
            response.setMstBranchId(karyawan.getCabang().getId());
            response.setCabangNama(karyawan.getCabang().getNama());
        }
        return response;
    }
}
