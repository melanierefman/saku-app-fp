package com.bcafinance.backend_saku.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Date;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "mst_karyawan")
@Getter
@Setter
@NoArgsConstructor
public class Karyawan {

    @Id
    @NotNull
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @Size(max = 100)
    @Column(name = "nama", nullable = false, length = 100)
    private String nama;

    @NotNull
    @Size(max = 50)
    @Column(name = "email", nullable = false, length = 50)
    private String email;

    @NotNull
    @Size(max = 20)
    @Column(name = "username", nullable = false, length = 20)
    private String username;

    @NotNull
    @Size(max = 255)
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @NotNull
    @Column(name = "status", nullable = false)
    private Boolean status;

    @NotNull
    @Column(name = "created_date", nullable = false)
    private Date createdDate;

    @NotNull
    @Column(name = "updated_date", nullable = false)
    private Date updatedDate;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mst_role_id", nullable = false)
    private Role role;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mst_branch_id", nullable = false)
    private Cabang cabang;
}