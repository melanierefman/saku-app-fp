package com.bcafinance.backend_saku.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "trx_otp")
@Getter
@Setter
@NoArgsConstructor
public class Otp {

    @Id
    @NotNull
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @Size(max = 6)
    @Column(name = "otp_code", nullable = false, length = 6)
    private String otpCode;

    @NotNull
    @Size(max = 50)
    @Column(name = "purpose", nullable = false, length = 50)
    private String purpose;

    @NotNull
    @Column(name = "is_used", nullable = false)
    private Boolean isUsed;

    @NotNull
    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt;

    @NotNull
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @NotNull
    @Column(name = "updated_date", nullable = false)
    private LocalDateTime updatedDate;

    @NotNull
    @Column(name = "mst_customer_id", nullable = false)
    private UUID mstCustomerId;
}