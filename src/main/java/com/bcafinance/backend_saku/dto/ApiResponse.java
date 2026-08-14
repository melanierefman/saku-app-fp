package com.bcafinance.backend_saku.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ApiResponse<T> {
    private Integer statusCode;
    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

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