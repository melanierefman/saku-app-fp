package com.bcafinance.backend_saku.features.superadmin.role.controller;

import com.bcafinance.backend_saku.core.dto.ApiResponse;
import com.bcafinance.backend_saku.features.superadmin.role.dto.AssignPermissionsRequest;
import com.bcafinance.backend_saku.features.superadmin.role.dto.RoleDetailResponse;
import com.bcafinance.backend_saku.features.superadmin.role.dto.RoleRequest;
import com.bcafinance.backend_saku.features.superadmin.role.dto.RoleResponse;
import com.bcafinance.backend_saku.features.superadmin.role.service.RoleService;
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
@RequestMapping({"/api/superadmin/role", "/api/master/role", "/api/role"})
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPERADMIN')")
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<RoleResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.success(roleService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RoleDetailResponse>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(roleService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RoleDetailResponse>> create(
            @Valid @RequestBody RoleRequest request) {
        return ResponseEntity.ok(ApiResponse.created(roleService.create(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RoleDetailResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody RoleRequest request) {
        return ResponseEntity.ok(ApiResponse.success(roleService.update(id, request)));
    }

    @PutMapping("/{id}/permissions")
    public ResponseEntity<ApiResponse<RoleDetailResponse>> assignPermissions(
            @PathVariable UUID id,
            @Valid @RequestBody AssignPermissionsRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                roleService.assignPermissions(id, request.getPermissionIds())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        roleService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
