package com.bcafinance.backend_saku.features.customer.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class NotifikasiResponse {

    private UUID id;
    private String type;
    private String channel;
    private String judul;
    private String pesan;
    private String status;

    @com.fasterxml.jackson.annotation.JsonProperty("isRead")
    private boolean isRead;

    private UUID pengajuanPinjamanId;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdDate;
}
