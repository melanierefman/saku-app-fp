package com.bcafinance.backend_saku.features.auth.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequest {

    @NotBlank(message = "Username atau email tidak boleh kosong")
    @JsonAlias({"username", "email"})
    private String identifier;

    @NotBlank(message = "Password tidak boleh kosong")
    private String password;
}
