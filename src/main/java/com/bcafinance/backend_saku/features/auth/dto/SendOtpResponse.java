package com.bcafinance.backend_saku.features.auth.dto;

import java.time.LocalDateTime;
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
public class SendOtpResponse {

    private String email;
    private String purpose;
    private Integer expiresInSeconds;
    private LocalDateTime expiredAt;
    private String message;
}
