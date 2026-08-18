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
@Table(name = "mst_customer")
@Getter
@Setter
@NoArgsConstructor
public class Customer {

    @Id
    @NotNull
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @Size(max = 20)
    @Column(name = "nik", nullable = false, length = 20)
    private String nik;

    @NotNull
    @Size(max = 150)
    @Column(name = "nama", nullable = false, length = 150)
    private String nama;

    @NotNull
    @Size(max = 50)
    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @NotNull
    @Size(max = 50)
    @Column(name = "email", nullable = false, length = 50)
    private String email;

    @NotNull
    @Size(max = 16)
    @Column(name = "no_hp", nullable = false, length = 16)
    private String noHp;

    @NotNull
    @Size(max = 255)
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @NotNull
    @Size(max = 150)
    @Column(name = "nama_rekening", nullable = false, length = 150)
    private String namaRekening;

    @NotNull
    @Column(name = "nama_bank", nullable = false, length = 50)
    private String namaBank;

    @NotNull
    @Size(max = 20)
    @Column(name = "no_rekening", nullable = false, length = 20)
    private String noRekening;

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
