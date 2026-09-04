package com.bcafinance.backend_saku.features.superadmin.permission.service;

import com.bcafinance.backend_saku.core.entity.Menu;
import com.bcafinance.backend_saku.core.entity.Permission;
import com.bcafinance.backend_saku.core.exception.BussinessRuleException;
import com.bcafinance.backend_saku.core.repository.MenuRepository;
import com.bcafinance.backend_saku.core.repository.PermissionRepository;
import com.bcafinance.backend_saku.features.superadmin.permission.dto.PermissionRequest;
import com.bcafinance.backend_saku.features.superadmin.permission.dto.PermissionResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository permissionRepository;
    private final MenuRepository menuRepository;

    @Transactional(readOnly = true)
    public List<PermissionResponse> findAll(UUID menuId) {
        List<Permission> list = (menuId != null)
                ? permissionRepository.findAllByMstMenuIdOrderByNamaAsc(menuId)
                : permissionRepository.findAllByOrderByCreatedDateDesc();

        return list.stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PermissionResponse findById(UUID id) {
        Permission p = permissionRepository.findById(id)
                .orElseThrow(() -> new BussinessRuleException("Data permission tidak ditemukan"));
        return toResponse(p);
    }

    @Transactional
    public PermissionResponse create(PermissionRequest request) {
        menuRepository.findById(request.getMstMenuId())
                .orElseThrow(() -> new BussinessRuleException("Menu dengan ID yang dipilih tidak ditemukan"));

        if (permissionRepository.existsByNamaIgnoreCaseAndMstMenuId(request.getNama(), request.getMstMenuId())) {
            throw new BussinessRuleException("Permission '" + request.getNama() + "' sudah ada pada menu ini");
        }

        Permission p = new Permission();
        p.setId(UUID.randomUUID());
        p.setNama(request.getNama());
        p.setResource(request.getResource().toUpperCase());
        p.setAction(request.getAction().toUpperCase());
        p.setMstMenuId(request.getMstMenuId());
        p.setCreatedDate(LocalDateTime.now());
        p.setUpdatedDate(LocalDateTime.now());

        return toResponse(permissionRepository.save(p));
    }

    @Transactional
    public List<PermissionResponse> createBulk(List<PermissionRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            return List.of();
        }

        List<PermissionResponse> result = new java.util.ArrayList<>();
        for (PermissionRequest req : requests) {
            Optional<Permission> existingOpt = permissionRepository
                    .findByNamaIgnoreCaseAndMstMenuId(req.getNama(), req.getMstMenuId());

            if (existingOpt.isPresent()) {
                Permission existing = existingOpt.get();
                existing.setResource(req.getResource().toUpperCase());
                existing.setAction(req.getAction().toUpperCase());
                existing.setUpdatedDate(LocalDateTime.now());
                result.add(toResponse(permissionRepository.save(existing)));
            } else {
                Permission p = new Permission();
                p.setId(UUID.randomUUID());
                p.setNama(req.getNama());
                p.setResource(req.getResource().toUpperCase());
                p.setAction(req.getAction().toUpperCase());
                p.setMstMenuId(req.getMstMenuId());
                p.setCreatedDate(LocalDateTime.now());
                p.setUpdatedDate(LocalDateTime.now());
                result.add(toResponse(permissionRepository.save(p)));
            }
        }
        return result;
    }

    @Transactional
    public PermissionResponse update(UUID id, PermissionRequest request) {
        Permission p = permissionRepository.findById(id)
                .orElseThrow(() -> new BussinessRuleException("Data permission tidak ditemukan"));

        menuRepository.findById(request.getMstMenuId())
                .orElseThrow(() -> new BussinessRuleException("Menu dengan ID yang dipilih tidak ditemukan"));

        if (!p.getNama().equalsIgnoreCase(request.getNama())
                && permissionRepository.existsByNamaIgnoreCaseAndMstMenuId(request.getNama(), request.getMstMenuId())) {
            throw new BussinessRuleException("Permission '" + request.getNama() + "' sudah ada pada menu ini");
        }

        p.setNama(request.getNama());
        p.setResource(request.getResource().toUpperCase());
        p.setAction(request.getAction().toUpperCase());
        p.setMstMenuId(request.getMstMenuId());
        p.setUpdatedDate(LocalDateTime.now());

        return toResponse(permissionRepository.save(p));
    }


    @Transactional
    public void delete(UUID id) {
        Permission p = permissionRepository.findById(id)
                .orElseThrow(() -> new BussinessRuleException("Data permission tidak ditemukan"));
        permissionRepository.delete(p);
    }

    private PermissionResponse toResponse(Permission p) {
        String menuNama = null;
        String menuPath = null;

        if (p.getMstMenuId() != null) {
            Optional<Menu> mOpt = menuRepository.findById(p.getMstMenuId());
            if (mOpt.isPresent()) {
                menuNama = mOpt.get().getNama();
                menuPath = mOpt.get().getPath();
            }
        }

        return PermissionResponse.builder()
                .id(p.getId())
                .nama(p.getNama())
                .resource(p.getResource())
                .action(p.getAction())
                .mstMenuId(p.getMstMenuId())
                .menuNama(menuNama)
                .menuPath(menuPath)
                .createdDate(p.getCreatedDate())
                .updatedDate(p.getUpdatedDate())
                .build();
    }
}
