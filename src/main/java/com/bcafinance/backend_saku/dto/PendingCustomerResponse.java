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
public class PendingCustomerResponse {

    private UUID id;
    private String nama;
    private String email;
    private String username;
    private String nik;
    private String noHp;
    private Boolean status;
    private String statusVerifikasi;
    private LocalDateTime tanggalPengajuan;

    public PendingCustomerResponse(UUID id, String nama, String email, String username, String nik, Boolean status) {
        this.id = id;
        this.nama = nama;
        this.email = email;
        this.username = username;
        this.nik = nik;
        this.status = status;
        this.statusVerifikasi = "PENDING";
    }
}

