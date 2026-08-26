package com.bcafinance.backend_saku.features.public_api.dto;

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
public class PublicCabangResponse {

    private UUID id;
    private String nama;
    private String kota;
    private Boolean isDefault;
}
