package com.bcafinance.backend_saku.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VerifikasiCustomerRequest {

    @NotBlank
    @Pattern(regexp = "APPROVED|REJECTED")
    private String statusVerifikasi;

    @NotBlank
    @Size(max = 500)
    private String catatanVerifikasi;
}
