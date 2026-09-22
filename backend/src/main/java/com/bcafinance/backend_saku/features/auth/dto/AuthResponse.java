package com.bcafinance.backend_saku.features.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {

    @Schema(description = "Tipe Token", example = "Bearer")
    private String tokenType;

    @Schema(description = "Access Token JWT", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJidWRpX3NhbnRvc28iLCJyb2xlIjoiUk9MRV9DVVNUT01FUiIsImV4cCI6MTgwMDAwMDAwMH0...")
    private String accessToken;

    @Schema(description = "Refresh Token JWT (Opsional)", example = "d9b23e45-6789-4bc5-a123-456789abcdef")
    private String refreshToken;

    @Schema(description = "Durasi Masa Aktif Token dalam Milidetik", example = "900000")
    private Long expiresIn;

    @Schema(description = "Informasi Profil Pengguna yang Login")
    private UserLoginProfile user;
}


