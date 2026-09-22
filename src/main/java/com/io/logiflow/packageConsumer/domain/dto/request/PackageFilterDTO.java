package com.io.logiflow.packageConsumer.domain.dto.request;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class PackageFilterDTO {
    private UUID uuid;
    private String trackingNumber;
    @PositiveOrZero(message = "min_weight cannot be negative")
    private BigDecimal minWeight;
    @PositiveOrZero(message = "max_weight cannot be negative")
    private BigDecimal maxWeight;
    @PositiveOrZero(message = "min_depth cannot be negative")
    private Integer minDepth;
    @PositiveOrZero(message = "max_depth cannot be negative")
    private Integer maxDepth;
    @PositiveOrZero(message = "min_height cannot be negative")
    private Integer minHeight;
    @PositiveOrZero(message = "max_height cannot be negative")
    private Integer maxHeight;
    @PositiveOrZero(message = "min_width cannot be negative")
    private Integer minWidth;
    @PositiveOrZero(message = "max_width cannot be negative")
    private Integer maxWidth;
}
