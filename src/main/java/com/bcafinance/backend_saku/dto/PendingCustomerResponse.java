package com.bcafinance.backend_saku.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PendingCustomerResponse {

    private UUID id;
    private String nama;
    private String email;
    private String username;
    private String nik;
    private Boolean status;
}
