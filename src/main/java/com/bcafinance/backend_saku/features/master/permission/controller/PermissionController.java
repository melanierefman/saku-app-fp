package com.bcafinance.backend_saku.features.master.permission.controller;

import com.bcafinance.backend_saku.core.dto.ApiResponse;
import com.bcafinance.backend_saku.features.master.permission.dto.PermissionRequest;
import com.bcafinance.backend_saku.features.master.permission.dto.PermissionResponse;
import com.bcafinance.backend_saku.features.master.permission.service.PermissionService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/master/permission", "/api/permission"})
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPERADMIN')")
public class PermissionController {

    private final PermissionService permissionService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<PermissionResponse>>> findAll(
            @RequestParam(required = false) UUID menuId) {
        return ResponseEntity.ok(ApiResponse.success(permissionService.findAll(menuId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PermissionResponse>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(permissionService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PermissionResponse>> create(
            @Valid @RequestBody PermissionRequest request) {
        return ResponseEntity.ok(ApiResponse.created(permissionService.create(request)));
    }

    @PostMapping("/bulk")
    public ResponseEntity<ApiResponse<List<PermissionResponse>>> createBulk(
            @RequestBody List<PermissionRequest> requests) {
        return ResponseEntity.ok(ApiResponse.created(permissionService.createBulk(requests)));
    }


    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PermissionResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody PermissionRequest request) {
        return ResponseEntity.ok(ApiResponse.success(permissionService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        permissionService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
