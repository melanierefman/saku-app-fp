package com.bcafinance.backend_saku.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AuthResponse {

    private String username;
    private String email;
    private String token;
}
