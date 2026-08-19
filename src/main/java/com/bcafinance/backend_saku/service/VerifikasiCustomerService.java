package com.bcafinance.backend_saku.service;

import com.bcafinance.backend_saku.dto.PendingCustomerResponse;
import com.bcafinance.backend_saku.dto.PlafondCalculationResponse;
import com.bcafinance.backend_saku.dto.VerifikasiCustomerRequest;
import com.bcafinance.backend_saku.dto.VerifikasiCustomerResponse;
import com.bcafinance.backend_saku.entity.Customer;
import com.bcafinance.backend_saku.entity.ScoringCustomer;
import com.bcafinance.backend_saku.entity.VerifikasiCustomer;
import com.bcafinance.backend_saku.exception.BussinessRuleException;
import com.bcafinance.backend_saku.repository.CustomerRepository;
import com.bcafinance.backend_saku.repository.ScoringCustomerRepository;
import com.bcafinance.backend_saku.repository.VerifikasiCustomerRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VerifikasiCustomerService {

    private final CustomerRepository customerRepository;
    private final ScoringCustomerRepository scoringRepository;
    private final VerifikasiCustomerRepository verifikasiRepository;
    private final PlafondService plafondService;

    public List<PendingCustomerResponse> findPending() {
        return customerRepository.findAllByStatusFalseOrderByCreatedDateAsc().stream()
                .map(customer -> new PendingCustomerResponse(
                        customer.getId(), customer.getNama(), customer.getEmail(),
                        customer.getUsername(), customer.getNik(), customer.getStatus()))
                .toList();
    }

    @Transactional
    public VerifikasiCustomerResponse verify(
            UUID customerId, UUID karyawanId, VerifikasiCustomerRequest request) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new BussinessRuleException("Customer tidak ditemukan"));
        ScoringCustomer scoring = scoringRepository.findFirstByMstCustomerIdOrderByCreatedDateDesc(customerId)
                .orElseThrow(() -> new BussinessRuleException("Data scoring customer belum tersedia"));

        String status = request.getStatusVerifikasi().toUpperCase();
        UUID plafondId = null;
        java.math.BigDecimal approvedAmount = java.math.BigDecimal.ZERO;
        String keputusan = scoring.getStatusScoring();

        if ("APPROVED".equals(status)) {
            if (scoring.getSkor() < 60) {
                throw new BussinessRuleException("Customer tidak memenuhi skor minimum untuk approval");
            }

            PlafondCalculationResponse calculation = plafondService.calculateApprovedAmount(
                    scoring.getPenghasilanBulanan(), scoring.getSkor());
            plafondId = calculation.getPlafondId();
            approvedAmount = calculation.getApprovedAmount();
            keputusan = calculation.getKeputusan();
            scoring.setMstPlafondId(plafondId);
            customer.setStatus(true);
        } else {
            scoring.setMstPlafondId(null);
            customer.setStatus(false);
        }

        scoring.setStatusScoring(keputusan);
        scoring.setUpdatedDate(LocalDateTime.now());
        scoringRepository.save(scoring);

        customer.setUpdatedDate(LocalDateTime.now());
        customerRepository.save(customer);

        VerifikasiCustomer verification = new VerifikasiCustomer();
        verification.setId(UUID.randomUUID());
        verification.setStatusVerifikasi(status);
        verification.setCatatanVerifikasi(request.getCatatanVerifikasi());
        verification.setCreatedDate(LocalDateTime.now());
        verification.setUpdatedDate(LocalDateTime.now());
        verification.setMstCustomerId(customerId);
        verification.setMstKaryawanId(karyawanId);
        verifikasiRepository.save(verification);

        return new VerifikasiCustomerResponse(
                customerId, status, request.getCatatanVerifikasi(), scoring.getSkor(),
                keputusan, plafondId, approvedAmount, customer.getStatus());
    }
}
