package com.bcafinance.backend_saku.features.auth.dto;

import java.time.LocalDateTime;
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
public class VerifyOtpResponse {

    private Boolean valid;
    private String message;
    private UUID customerId;
    private String email;
    private String purpose;
    private LocalDateTime verifiedAt;
}
