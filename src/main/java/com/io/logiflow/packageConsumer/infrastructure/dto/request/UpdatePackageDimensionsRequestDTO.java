package com.io.logiflow.packageConsumer.infrastructure.dto.request;

import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record UpdatePackageDimensionsRequestDTO(

        @Positive(message = "Weight must be positive")
        BigDecimal weight,

        @Positive(message = "Length must be positive")
        Integer length,

        @Positive(message = "Width must be positive")
        Integer width,

        @Positive(message = "Height must be positive")
        Integer height
) {
}
