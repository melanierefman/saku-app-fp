package com.bcafinance.backend_saku.features.superadmin.menu.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class MenuRequest {

    @NotBlank(message = "Nama menu tidak boleh kosong")
    @Size(max = 50, message = "Nama menu maksimal 50 karakter")
    private String nama;

    @NotBlank(message = "Path menu tidak boleh kosong")
    @Size(max = 150, message = "Path menu maksimal 150 karakter")
    private String path;

    @NotNull(message = "Status menu harus ditentukan (true/false)")
    private Boolean status;
}
