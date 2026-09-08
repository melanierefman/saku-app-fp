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
public class UpdateRekeningRequest {

    @NotBlank(message = "Nama bank tidak boleh kosong")
    @Size(max = 50, message = "Nama bank maksimal 50 karakter")
    private String namaBank;

    @NotBlank(message = "Nomor rekening tidak boleh kosong")
    @Size(max = 20, message = "Nomor rekening maksimal 20 digit")
    private String noRekening;

    @NotBlank(message = "Nama pemilik rekening tidak boleh kosong")
    @Size(max = 150, message = "Nama pemilik rekening maksimal 150 karakter")
    private String namaRekening;
}
