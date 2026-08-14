package com.bcafinance.backend_saku.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "trx_scoring_customer")
@Getter
@Setter
@NoArgsConstructor
public class ScoringCustomer {

    @Id
    @NotNull
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @Size(max = 100)
    @Column(name = "pekerjaan", nullable = false, length = 100)
    private String pekerjaan;

    @NotNull
    @Size(max = 50)
    @Column(name = "status_pekerjaan", nullable = false, length = 50)
    private String statusPekerjaan;

    @NotNull
    @Digits(integer = 13, fraction = 2)
    @Column(name = "penghasilan_bulanan", nullable = false, precision = 15, scale = 2)
    private BigDecimal penghasilanBulanan;

    @NotNull
    @Column(name = "lama_bekerja_bulan", nullable = false)
    private Integer lamaBekerjaBulan;

    @NotNull
    @Column(name = "lama_jadi_nasabah_bulan", nullable = false)
    private Integer lamaJadiNasabahBulan;

    @NotNull
    @Digits(integer = 13, fraction = 2)
    @Column(name = "total_cicilan_lain_bulanan", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalCicilanLainBulanan;

    @NotNull
    @Column(name = "skor", nullable = false)
    private Integer skor;

    @NotNull
    @Size(max = 20)
    @Column(name = "status_scoring", nullable = false, length = 20)
    private String statusScoring;

    @NotNull
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @NotNull
    @Column(name = "updated_date", nullable = false)
    private LocalDateTime updatedDate;

    @NotNull
    @Column(name = "mst_customer_id", nullable = false)
    private UUID mstCustomerId;

    @NotNull
    @Column(name = "mst_plafond_id", nullable = false)
    private UUID mstPlafondId;
}