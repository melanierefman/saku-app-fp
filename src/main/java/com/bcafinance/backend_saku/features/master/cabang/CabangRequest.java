package com.bcafinance.backend_saku.features.master.cabang;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CabangRequest {

    @NotBlank
    @Size(max = 50)
    private String nama;

    @NotBlank
    @Size(max = 50)
    private String kota;

    @NotNull
    private Boolean isDefault;

    @NotNull
    private Boolean status;
}
