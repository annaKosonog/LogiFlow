package com.io.logiflow.packageConsumer.exception.web.api.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL) // Nie wysyłaj nulli w JSON-ie (np. errors przy sukcesie)
public class ApiResponse<T> {
    private final T data;
    private final Object errors;
    private final LocalDateTime timestamp;


    private ApiResponse(T data, Object errors) {
        this.data = data;
        this.errors = errors;
        this.timestamp = LocalDateTime.now();
    }

    // Fabric method  (Clean Code)
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(data, null);
    }

    public static <T> ApiResponse<T> error( Object errors) {
        return new ApiResponse<>(null,  errors);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(null, message);
    }

}
