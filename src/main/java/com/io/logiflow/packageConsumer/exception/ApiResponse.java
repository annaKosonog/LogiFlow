package com.io.logiflow.packageConsumer.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL) // Nie wysyłaj nulli w JSON-ie (np. errors przy sukcesie)
public class ApiResponse<T> {
    private final boolean success;
    private final String message;
    private final T data;
    private final Object errors;
    private final LocalDateTime timestamp;


    private ApiResponse(boolean success, String message, T data, Object errors) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.errors = errors;
        this.timestamp = LocalDateTime.now();
    }

    // Statyczne metody fabrykujące (Clean Code)
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, message, data, null);
    }

    public static <T> ApiResponse<T> success(T data) {
        return success(data, "Success");
    }

    public static <T> ApiResponse<T> error(String message, Object errors) {
        return new ApiResponse<>(false, message, null, errors);
    }

    public static <T> ApiResponse<T> error(String message) {
        return error(message, null);
    }

}
