package com.bcafinance.backend_saku.dto;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PlafondCalculationResponse {

    private UUID plafondId;
    private String plafondNama;
    private String keputusan;
    private Integer persentaseApproval;
    private BigDecimal plafondMaksimal;
    private BigDecimal approvedAmount;
}
