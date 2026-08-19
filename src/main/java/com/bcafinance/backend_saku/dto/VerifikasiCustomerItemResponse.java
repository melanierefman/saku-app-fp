package com.bcafinance.backend_saku.dto;

import java.time.LocalDateTime;
import java.util.UUID;
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
public class VerifikasiCustomerItemResponse {

    private UUID customerId;
    private String namaCustomer;
    private String nik;
    private String email;
    private String noHp;
    private LocalDateTime tanggalPengajuan;
    private String statusVerifikasi;
    private String catatanVerifikasi;
    private LocalDateTime tanggalVerifikasi;
}
