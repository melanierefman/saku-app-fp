package com.bcafinance.backend_saku.features.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserLoginProfile {


    private UUID id;
    private String username;
    private String nama;
    private String email;
    private String noHp;
    private String role;
    private String tipe;
    private Boolean status;
    private Boolean isKycVerified;
    private String cabang;
    private List<String> permissions;
}
