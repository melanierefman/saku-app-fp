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
@Table(name = "trx_notifikasi")
@Getter
@Setter
@NoArgsConstructor
public class Notifikasi {

    @Id
    @NotNull
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @Size(max = 50)
    @Column(name = "type", nullable = false, length = 50)
    private String type;

    @NotNull
    @Size(max = 50)
    @Column(name = "channel", nullable = false, length = 50)
    private String channel;

    @NotNull
    @Size(max = 50)
    @Column(name = "judul", nullable = false, length = 50)
    private String judul;

    @NotNull
    @Size(max = 500)
    @Column(name = "pesan", nullable = false, length = 500)
    private String pesan;

    @NotNull
    @Size(max = 50)
    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @NotNull
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @NotNull
    @Column(name = "updated_date", nullable = false)
    private LocalDateTime updatedDate;

    @NotNull
    @Column(name = "mst_customer_id", nullable = false)
    private UUID mstCustomerId;

    @Column(name = "trx_pengajuan_pinjaman_id")
    private UUID trxPengajuanPinjamanId;
}
