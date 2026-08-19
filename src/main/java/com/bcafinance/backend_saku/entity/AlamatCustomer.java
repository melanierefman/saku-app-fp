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
@Table(name = "mst_alamat_customer")
@Getter
@Setter
@NoArgsConstructor
public class AlamatCustomer {

    @Id
    @NotNull
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @Size(max = 50)
    @Column(name = "jenis_alamat", nullable = false, length = 50)
    private String jenisAlamat;

    @NotNull
    @Size(max = 100)
    @Column(name = "alamat_lengkap", nullable = false, length = 100)
    private String alamatLengkap;

    @NotNull
    @Size(max = 20)
    @Column(name = "rt", nullable = false, length = 20)
    private String rt;

    @NotNull
    @Size(max = 20)
    @Column(name = "rw", nullable = false, length = 20)
    private String rw;

    @NotNull
    @Size(max = 50)
    @Column(name = "kelurahan", nullable = false, length = 50)
    private String kelurahan;

    @NotNull
    @Size(max = 50)
    @Column(name = "kecamatan", nullable = false, length = 50)
    private String kecamatan;

    @NotNull
    @Size(max = 50)
    @Column(name = "kota_kabupaten", nullable = false, length = 50)
    private String kotaKabupaten;

    @NotNull
    @Size(max = 50)
    @Column(name = "provinsi", nullable = false, length = 50)
    private String provinsi;

    @NotNull
    @Size(max = 10)
    @Column(name = "kode_pos", nullable = false, length = 10)
    private String kodePos;

    @NotNull
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @NotNull
    @Column(name = "updated_date", nullable = false)
    private LocalDateTime updatedDate;

    @ManyToOne
    @JoinColumn(name = "mst_customer_id", nullable = false)
    private Customer customer;
}