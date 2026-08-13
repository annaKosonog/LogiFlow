package com.io.logiflow.packageConsumer.domain.validator;

import java.math.BigDecimal;

public final class DimensionValidator {
    private DimensionValidator() {
    }

    public  static void validatePositiveDimensions(BigDecimal weight, Integer length, Integer width, Integer height) {
        if (weight != null && weight.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Weight must be greater than zero");
        }
        if (length != null && length <= 0) {
            throw new IllegalArgumentException("Length/Depth must be greater than zero");
        }
        if (width != null && width <= 0) {
            throw new IllegalArgumentException("Width must be greater than zero");
        }
        if (height != null && height <= 0) {
            throw new IllegalArgumentException("Height must be greater than zero");
        }
    }

    public static void validateNotBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
    }


    }


