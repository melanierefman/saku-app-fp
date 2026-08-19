package com.bcafinance.backend_saku.controller;

import com.bcafinance.backend_saku.dto.ApiResponse;
import com.bcafinance.backend_saku.dto.CabangRequest;
import com.bcafinance.backend_saku.dto.CabangResponse;
import com.bcafinance.backend_saku.service.CabangService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cabang")
@RequiredArgsConstructor
public class CabangController {

    private final CabangService cabangService;

    @PostMapping
    public ResponseEntity<ApiResponse<CabangResponse>> create(
            @Valid @RequestBody CabangRequest request) {
        return ResponseEntity.ok(ApiResponse.created(cabangService.create(request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CabangResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.success(cabangService.findAll()));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<CabangResponse>>> findAllActive() {
        return ResponseEntity.ok(ApiResponse.success(cabangService.findAllActive()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CabangResponse>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(cabangService.findById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CabangResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody CabangRequest request) {
        return ResponseEntity.ok(ApiResponse.updated(cabangService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        cabangService.delete(id);
        return ResponseEntity.ok(ApiResponse.deleted());
    }
}
