package com.bcafinance.backend_saku.core.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "Jenis Alamat (KTP / DOMISILI)", example = "KTP")
    private String jenisAlamat;

    @Schema(description = "Jalan dan Nomor Rumah", example = "Jl. Jenderal Sudirman No. 45")
    private String alamatLengkap;

    @Schema(description = "Nomor RT", example = "005")
    private String rt;

    @Schema(description = "Nomor RW", example = "002")
    private String rw;

    @Schema(description = "Nama Kelurahan / Desa", example = "Karet Semanggi")
    private String kelurahan;

    @Schema(description = "Nama Kecamatan", example = "Setiabudi")
    private String kecamatan;

    @Schema(description = "Nama Kota / Kabupaten", example = "Jakarta Selatan")
    private String kotaKabupaten;

    @Schema(description = "Nama Provinsi", example = "DKI Jakarta")
    private String provinsi;

    @Schema(description = "Kode Pos (5 digit)", example = "12930")
    private String kodePos;

    @Schema(description = "Alamat format lengkap satu baris", example = "Jl. Jenderal Sudirman No. 45, RT 005 / RW 002, Karet Semanggi, Setiabudi, Jakarta Selatan, DKI Jakarta 12930")
    private String formattedAddress;
}
