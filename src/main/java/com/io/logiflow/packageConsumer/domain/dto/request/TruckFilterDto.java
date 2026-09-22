package com.io.logiflow.packageConsumer.domain.dto.request;


import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class TruckFilterDto {

    private UUID uuid;
    @PositiveOrZero(message = "min_width cannot be negative")
    private Integer minWidth;
    @PositiveOrZero(message = "max_width cannot be negative")
    private Integer maxWidth;
    @PositiveOrZero(message = "min_height cannot be negative")
    private Integer minHeight;
    @PositiveOrZero(message = "max_height cannot be negative")
    private Integer maxHeight;
    @PositiveOrZero(message = "min_length cannot be negative")
    private Integer minLength;
    @PositiveOrZero(message = "max_length cannot be negative")
    private Integer maxLength;
    @PositiveOrZero(message = "min_max_payload cannot be negative")
    private BigDecimal minMaxPayload;
    @PositiveOrZero(message = "max_max_payload cannot be negative")
    private BigDecimal maxMaxPayload;
    @PositiveOrZero(message = "min_weight cannot be negative")
    private BigDecimal minWeight;
    @PositiveOrZero(message = "max_weight cannot be negative")
    private BigDecimal maxWeight;
}
