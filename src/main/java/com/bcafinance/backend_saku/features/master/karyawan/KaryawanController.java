package com.bcafinance.backend_saku.features.master.karyawan;

import com.bcafinance.backend_saku.core.dto.ApiResponse;
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
@RequestMapping("/api/karyawan")
@RequiredArgsConstructor
public class KaryawanController {

    private final KaryawanService karyawanService;

    @PostMapping
    public ResponseEntity<ApiResponse<KaryawanResponse>> create(
            @Valid @RequestBody KaryawanRequest request) {
        return ResponseEntity.ok(ApiResponse.created(karyawanService.create(request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<KaryawanResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.success(karyawanService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<KaryawanResponse>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(karyawanService.findById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<KaryawanResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody KaryawanRequest request) {
        return ResponseEntity.ok(ApiResponse.updated(karyawanService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        karyawanService.delete(id);
        return ResponseEntity.ok(ApiResponse.deleted());
    }
}
