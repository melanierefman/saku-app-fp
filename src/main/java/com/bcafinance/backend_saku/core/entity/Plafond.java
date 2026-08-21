package com.bcafinance.backend_saku.core.entity;

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
@Table(name = "mst_plafond")
@Getter
@Setter
@NoArgsConstructor
public class Plafond {

    @Id
    @NotNull
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @Size(max = 50)
    @Column(name = "nama", nullable = false, length = 50)
    private String nama;

    @NotNull
    @Digits(integer = 13, fraction = 2)
    @Column(name = "min_pendapatan", nullable = false, precision = 15, scale = 2)
    private BigDecimal minPendapatan;

    @NotNull
    @Digits(integer = 13, fraction = 2)
    @Column(name = "plafond_maksimal", nullable = false, precision = 15, scale = 2)
    private BigDecimal plafondMaksimal;

    @NotNull
    @Digits(integer = 13, fraction = 2)
    @Column(name = "min_plafond", nullable = false, precision = 15, scale = 2)
    private BigDecimal minPlafond;

    @NotNull
    @Digits(integer = 13, fraction = 2)
    @Column(name = "max_plafond", nullable = false, precision = 15, scale = 2)
    private BigDecimal maxPlafond;

    @NotNull
    @Column(name = "min_skor", nullable = false)
    private Integer minSkor;

    @NotNull
    @Column(name = "max_skor", nullable = false)
    private Integer maxSkor;

    @NotNull
    @Digits(integer = 1, fraction = 2)
    @Column(name = "bunga", nullable = false, precision = 3, scale = 2)
    private BigDecimal bunga;

    @NotNull
    @Digits(integer = 13, fraction = 2)
    @Column(name = "biaya_admin", nullable = false, precision = 15, scale = 2)
    private BigDecimal biayaAdmin;

    @NotNull
    @Column(name = "status", nullable = false)
    private Boolean status;

    @NotNull
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @NotNull
    @Column(name = "updated_date", nullable = false)
    private LocalDateTime updatedDate;
}