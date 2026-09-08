package com.bcafinance.backend_saku.features.customer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDomisiliRequest {

    @NotBlank(message = "Alamat lengkap tidak boleh kosong")
    @Size(max = 100, message = "Alamat lengkap maksimal 100 karakter")
    private String alamatLengkap;

    @NotBlank(message = "RT tidak boleh kosong")
    @Size(max = 20, message = "RT maksimal 20 karakter")
    private String rt;

    @NotBlank(message = "RW tidak boleh kosong")
    @Size(max = 20, message = "RW maksimal 20 karakter")
    private String rw;

    @NotBlank(message = "Kelurahan tidak boleh kosong")
    @Size(max = 50, message = "Kelurahan maksimal 50 karakter")
    private String kelurahan;

    @NotBlank(message = "Kecamatan tidak boleh kosong")
    @Size(max = 50, message = "Kecamatan maksimal 50 karakter")
    private String kecamatan;

    @NotBlank(message = "Kota/Kabupaten tidak boleh kosong")
    @Size(max = 50, message = "Kota/Kabupaten maksimal 50 karakter")
    private String kotaKabupaten;

    @NotBlank(message = "Provinsi tidak boleh kosong")
    @Size(max = 50, message = "Provinsi maksimal 50 karakter")
    private String provinsi;

    @NotBlank(message = "Kode pos tidak boleh kosong")
    @Size(max = 10, message = "Kode pos maksimal 10 karakter")
    private String kodePos;
}
