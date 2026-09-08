package com.bcafinance.backend_saku.features.superadmin.role.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class RoleRequest {

    @NotBlank(message = "Nama role tidak boleh kosong")
    @Size(max = 50, message = "Nama role maksimal 50 karakter")
    private String nama;

    @NotNull(message = "Status role harus ditentukan (true/false)")
    private Boolean status;

    private List<UUID> permissionIds;
}
