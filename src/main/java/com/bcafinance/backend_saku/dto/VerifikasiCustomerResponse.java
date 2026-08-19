package com.bcafinance.backend_saku.dto;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VerifikasiCustomerResponse {

    private UUID customerId;
    private String statusVerifikasi;
    private String catatanVerifikasi;
    private Integer skor;
    private String keputusanScoring;
    private UUID plafondId;
    private BigDecimal approvedAmount;
    private Boolean customerActive;
}
