package com.bcafinance.backend_saku.features.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserLoginProfile {

    @Schema(description = "ID Pengguna", example = "us123e45-6789-4bc5-a123-456789abcdef")
    private UUID id;

    @Schema(description = "Username login", example = "budi_santoso")
    private String username;

    @Schema(description = "Nama Lengkap Pengguna", example = "Budi Santoso")
    private String nama;

    @Schema(description = "Alamat Email", example = "budi.santoso@example.com")
    private String email;

    @Schema(description = "Nomor Handphone", example = "081234567890")
    private String noHp;

    @Schema(description = "Role Pengguna", example = "CUSTOMER")
    private String role;

    @Schema(description = "Tipe Pengguna (CUSTOMER / KARYAWAN)", example = "CUSTOMER")
    private String tipe;

    @Schema(description = "Status Keaktifan Akun", example = "true")
    private Boolean status;

    @Schema(description = "Status Verifikasi KYC (Khusus Customer)", example = "true")
    private Boolean isKycVerified;

    @Schema(description = "Nama Cabang Terkait", example = "Cabang Jakarta Pusat")
    private String cabang;

    @Schema(description = "Daftar Kode Izin / Permission")
    private List<String> permissions;
}
