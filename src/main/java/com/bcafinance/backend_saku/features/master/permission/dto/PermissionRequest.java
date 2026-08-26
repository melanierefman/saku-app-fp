package com.bcafinance.backend_saku.features.master.permission.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class PermissionRequest {

    @NotBlank(message = "Nama permission tidak boleh kosong")
    @Size(max = 50, message = "Nama permission maksimal 50 karakter")
    private String nama;

    @NotBlank(message = "Resource permission tidak boleh kosong")
    @Size(max = 50, message = "Resource permission maksimal 50 karakter")
    private String resource;

    @NotBlank(message = "Action permission tidak boleh kosong")
    @Size(max = 50, message = "Action permission maksimal 50 karakter")
    private String action;

    @NotNull(message = "Menu ID harus dipilih")
    private UUID mstMenuId;
}
