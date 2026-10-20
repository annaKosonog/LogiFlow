package com.io.logiflow.packageConsumer.domain.validator;

import com.io.logiflow.packageConsumer.infrastructure.dto.response.DimensionsDTO;

import java.math.BigDecimal;


public final class DimensionValidator {
    private DimensionValidator() {
    }

    public static void validatePositiveDimensions(NamedDimension... dimensions) {
        for (NamedDimension dim : dimensions) {
            if (dim.value == null || dim.name == null || dim.name.isBlank()) {
                continue;
            }
            if (toBigDecimal(dim.value).compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException(dim.name() + " must be greater than zero");
            }
        }
    }

    private static BigDecimal toBigDecimal(Number number) {
        if (number instanceof BigDecimal bd) {
            return bd;
        }
        return new BigDecimal(number.toString());
    }

    public static void validateNotBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
    }

    public static void validateDimensions(DimensionsDTO dimensions, String prefix) {
        DimensionValidator.validatePositiveDimensions(
                new DimensionValidator.NamedDimension(prefix + " width", dimensions.getWidth()),
                new DimensionValidator.NamedDimension(prefix + " height", dimensions.getHeight()),
                new DimensionValidator.NamedDimension(prefix + " depth", dimensions.getDepth_length())
        );
    }


    public record NamedDimension(String name, Number value) {
    }

    public static void validatePackage(Number weight, Number length, Number width, Number height) {
        validatePositiveDimensions(
                new NamedDimension("Weight", weight),
                new NamedDimension("Length", length),
                new NamedDimension("Width", width),
                new NamedDimension("Height", height));
    }

}


