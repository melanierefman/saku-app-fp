package com.bcafinance.backend_saku.core.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ApiResponse<T> {

    @Schema(description = "Kode status HTTP respons", example = "200")
    private Integer statusCode;

    @Schema(description = "Pesan keterangan respons", example = "Success")
    private String message;

    @Schema(description = "Data payload respons")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    @Schema(description = "Keterangan rincian error jika ada")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object errors;

    public static <T>ApiResponse<T> success(Integer statusCode, String message, T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setStatusCode(statusCode);
        response.setMessage(message);
        response.setData(data);
        return response;
    }

    public static <T> ApiResponse<T> success(T data) {
        return success(200, "Success", data);
    }

    public static <T> ApiResponse<T> created(T data) {
        return success(201, "Data berhasil dibuat", data);
    }

    public static <T> ApiResponse<T> updated(T data) {
        return success(200, "Data berhasil diupdate", data);
    }

    public static <T> ApiResponse<T> deleted() {
        return success(200, "Data berhasil dihapus", null);
    }
}