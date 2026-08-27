package com.bcafinance.backend_saku.core.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "trx_verifikasi_customer")
@Getter
@Setter
@NoArgsConstructor
public class VerifikasiCustomer {

    @Id
    @NotNull
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @Size(max = 20)
    @Column(name = "status_verifikasi", nullable = false, length = 20)
    private String statusVerifikasi;

    @NotNull
    @Size(max = 1000)
    @Column(name = "catatan_verifikasi", nullable = false, length = 1000)
    private String catatanVerifikasi;


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
    @Column(name = "mst_karyawan_id", nullable = false)
    private UUID mstKaryawanId;
}
