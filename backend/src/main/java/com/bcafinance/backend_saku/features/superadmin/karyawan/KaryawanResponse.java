package com.bcafinance.backend_saku.features.superadmin.karyawan;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class KaryawanResponse {

    private UUID id;
    private String nama;
    private String email;
    private String username;
    private Boolean status;
    private Date createdDate;
    private Date updatedDate;
    private UUID mstRoleId;
    private String roleNama;
    private UUID mstBranchId;
    private String cabangNama;
}


