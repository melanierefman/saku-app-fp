package com.bcafinance.backend_saku.features.superadmin.menu.service;

import com.bcafinance.backend_saku.core.entity.Menu;
import com.bcafinance.backend_saku.core.exception.BussinessRuleException;
import com.bcafinance.backend_saku.core.repository.MenuRepository;
import com.bcafinance.backend_saku.features.superadmin.menu.dto.MenuRequest;
import com.bcafinance.backend_saku.features.superadmin.menu.dto.MenuResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;

    @Transactional(readOnly = true)
    public List<MenuResponse> findAll() {
        return menuRepository.findAllByOrderByCreatedDateDesc().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public MenuResponse findById(UUID id) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new BussinessRuleException("Data menu tidak ditemukan"));
        return toResponse(menu);
    }

    @Transactional
    public MenuResponse create(MenuRequest request) {
        if (menuRepository.existsByPathIgnoreCase(request.getPath())) {
            throw new BussinessRuleException("Path menu sudah digunakan");
        }

        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setNama(request.getNama());
        menu.setPath(request.getPath());
        menu.setStatus(request.getStatus() != null ? request.getStatus() : true);
        menu.setCreatedDate(LocalDateTime.now());
        menu.setUpdatedDate(LocalDateTime.now());

        return toResponse(menuRepository.save(menu));
    }

    @Transactional
    public List<MenuResponse> createBulk(List<MenuRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            return List.of();
        }

        List<MenuResponse> result = new java.util.ArrayList<>();
        for (MenuRequest req : requests) {
            Optional<Menu> existingOpt = menuRepository.findByPathIgnoreCase(req.getPath());
            if (existingOpt.isPresent()) {
                Menu existing = existingOpt.get();
                existing.setNama(req.getNama());
                if (req.getStatus() != null) {
                    existing.setStatus(req.getStatus());
                }
                existing.setUpdatedDate(LocalDateTime.now());
                result.add(toResponse(menuRepository.save(existing)));
            } else {
                Menu menu = new Menu();
                menu.setId(UUID.randomUUID());
                menu.setNama(req.getNama());
                menu.setPath(req.getPath());
                menu.setStatus(req.getStatus() != null ? req.getStatus() : true);
                menu.setCreatedDate(LocalDateTime.now());
                menu.setUpdatedDate(LocalDateTime.now());
                result.add(toResponse(menuRepository.save(menu)));
            }
        }
        return result;
    }


    @Transactional
    public MenuResponse update(UUID id, MenuRequest request) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new BussinessRuleException("Data menu tidak ditemukan"));

        if (!menu.getPath().equalsIgnoreCase(request.getPath()) && menuRepository.existsByPathIgnoreCase(request.getPath())) {
            throw new BussinessRuleException("Path menu sudah digunakan oleh menu lain");
        }

        menu.setNama(request.getNama());
        menu.setPath(request.getPath());
        if (request.getStatus() != null) {
            menu.setStatus(request.getStatus());
        }
        menu.setUpdatedDate(LocalDateTime.now());

        return toResponse(menuRepository.save(menu));
    }

    @Transactional
    public void delete(UUID id) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new BussinessRuleException("Data menu tidak ditemukan"));
        menuRepository.delete(menu);
    }

    private MenuResponse toResponse(Menu m) {
        return MenuResponse.builder()
                .id(m.getId())
                .nama(m.getNama())
                .path(m.getPath())
                .status(m.getStatus())
                .createdDate(m.getCreatedDate())
                .updatedDate(m.getUpdatedDate())
                .build();
    }
}
