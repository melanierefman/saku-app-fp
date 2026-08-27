package com.bcafinance.backend_saku.features.marketing.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
public class ReviewPengajuanRequest {

    @NotBlank(message = "Hasil review tidak boleh kosong (DISETUJUI / DITOLAK / PERLU_REVISI)")
    private String hasilReview;

    @NotBlank(message = "Catatan review tidak boleh kosong")
    @Size(max = 1000, message = "Catatan review maksimal 1000 karakter")
    private String catatan;

}
