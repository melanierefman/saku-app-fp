package com.bcafinance.backend_saku.features.master.karyawan;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class KaryawanRequest {

    @NotBlank
    @Size(max = 100)
    private String nama;

    @NotBlank
    @Email
    @Size(max = 50)
    private String email;

    @NotBlank
    @Size(max = 20)
    private String username;

    @NotBlank
    @Size(max = 20)
    private String password;

    @NotNull
    private Boolean status;

    @NotNull
    private UUID mstRoleId;

    @NotNull
    private UUID mstBranchId;
}
