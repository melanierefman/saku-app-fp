package com.bcafinance.backend_saku.core.dto;

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
public class AlamatDetailResponse {

    private String jenisAlamat;
    private String alamatLengkap;
    private String rt;
    private String rw;
    private String kelurahan;
    private String kecamatan;
    private String kotaKabupaten;
    private String provinsi;
    private String kodePos;
    private String formattedAddress;
}
