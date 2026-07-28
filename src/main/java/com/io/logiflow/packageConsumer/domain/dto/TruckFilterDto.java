package com.io.logiflow.packageConsumer.domain.dto;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class TruckFilterDto {

    private UUID uuid;
    private Integer minWidth;
    private Integer maxWidth;
    private Integer minHeight;
    private Integer maxHeight;
    private Integer minLength;
    private Integer maxLength;
    private BigDecimal minMaxPayload;
    private BigDecimal maxMaxPayload;
    private BigDecimal minWeight;
    private BigDecimal maxWeight;
}
