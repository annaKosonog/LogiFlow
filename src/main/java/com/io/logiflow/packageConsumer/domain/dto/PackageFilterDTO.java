package com.io.logiflow.packageConsumer.domain.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class PackageFilterDTO {
    private UUID uuid;
    private String trackingNumber;
    private BigDecimal minWeight;
    private BigDecimal maxWeight;
    private Integer minDepth;
    private Integer maxDepth;
    private Integer minHeight;
    private Integer maxHeight;
    private Integer minWidth;
    private Integer maxWidth;
}
