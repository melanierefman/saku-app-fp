package com.bcafinance.backend_saku.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AuthRequest {

    private String identifier;
    private String password;
}
