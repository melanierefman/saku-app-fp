package com.bcafinance.backend_saku.features.master.plafond;

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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/plafond")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPERADMIN')")
public class PlafondController {


    private final PlafondService plafondService;

    @PostMapping
    public ResponseEntity<ApiResponse<PlafondResponse>> create(
            @Valid @RequestBody PlafondRequest request) {
        return ResponseEntity.ok(ApiResponse.created(plafondService.create(request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PlafondResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.success(plafondService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PlafondResponse>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(plafondService.findById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PlafondResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody PlafondRequest request) {
        return ResponseEntity.ok(ApiResponse.updated(plafondService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        plafondService.delete(id);
        return ResponseEntity.ok(ApiResponse.deleted());
    }

    @PostMapping("/calculate")
    public ResponseEntity<ApiResponse<PlafondCalculationResponse>> calculate(
            @Valid @RequestBody PlafondCalculationRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                plafondService.calculateApprovedAmount(request.getPendapatan(), request.getSkorAkhir())));
    }
}
