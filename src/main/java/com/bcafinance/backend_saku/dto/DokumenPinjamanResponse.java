package com.bcafinance.backend_saku.dto;

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
public class DokumenPinjamanResponse {

    private UUID id;
    private String docType;
    private String fileUrl;
    private LocalDateTime createdDate;
}
