package com.bcafinance.backend_saku.features.backoffice.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PencairanRequest {

    private String catatan;
    private BigDecimal jumlahPencairan;
}
