package com.bcafinance.backend_saku.features.master.cabang;

import java.io.Serializable;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CabangResponse implements Serializable {


    private UUID id;
    private String nama;
    private String kota;
    private Boolean isDefault;
    private Boolean status;
}
