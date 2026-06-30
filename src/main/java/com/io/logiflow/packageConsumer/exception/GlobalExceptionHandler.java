package com.io.logiflow.packageConsumer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException exception) {
        // Zwraca status 400 Bad Request i wiadomość z błędu (np. "Truck loading failed")
        return ResponseEntity.badRequest().body(exception.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleNotFound(ResourceNotFoundException exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(exception.getMessage());
    }

    @ExceptionHandler(PackageValidationException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalState(PackageValidationException ex) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.CONFLICT.value()); // Status 409 Conflict idealnie tu pasuje
        response.put("error", "Conflict in logistics process");
        response.put("message", ex.getMessage()); // Tu wpadnie nasz tekst o konkretnej paczce!

        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }
}
