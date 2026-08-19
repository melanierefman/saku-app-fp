package com.bcafinance.backend_saku.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScoringBreakdown {

    private String dbrDetail;
    private String pendapatanDetail;
    private String lamaBekerjaDetail;
    private String statusPekerjaanDetail;
    private String lamaNasabahDetail;
}
