package com.bcafinance.backend_saku.features.superadmin.menu.controller;

import com.bcafinance.backend_saku.core.dto.ApiResponse;
import com.bcafinance.backend_saku.features.superadmin.menu.dto.MenuRequest;
import com.bcafinance.backend_saku.features.superadmin.menu.dto.MenuResponse;
import com.bcafinance.backend_saku.features.superadmin.menu.service.MenuService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/superadmin/menu", "/api/master/menu", "/api/menu"})
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPERADMIN')")
public class MenuController {

    private final MenuService menuService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<MenuResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.success(menuService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MenuResponse>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(menuService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MenuResponse>> create(@Valid @RequestBody MenuRequest request) {
        return ResponseEntity.ok(ApiResponse.created(menuService.create(request)));
    }

    @PostMapping("/bulk")
    public ResponseEntity<ApiResponse<List<MenuResponse>>> createBulk(@RequestBody List<MenuRequest> requests) {
        return ResponseEntity.ok(ApiResponse.created(menuService.createBulk(requests)));
    }


    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MenuResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody MenuRequest request) {
        return ResponseEntity.ok(ApiResponse.success(menuService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        menuService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
