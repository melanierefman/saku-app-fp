package com.bcafinance.backend_saku.features.superadmin.plafond;

import java.io.Serializable;
import java.math.BigDecimal;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PlafondResponse implements Serializable {

    private UUID id;
    private String nama;
    private BigDecimal minPendapatan;
    private BigDecimal plafondMaksimal;
    private BigDecimal minPlafond;
    private BigDecimal maxPlafond;
    private Integer minSkor;
    private Integer maxSkor;
    private BigDecimal bunga;
    private BigDecimal biayaAdmin;
    private Boolean status;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
