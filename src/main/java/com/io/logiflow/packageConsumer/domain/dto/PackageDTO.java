package com.io.logiflow.packageConsumer.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PackageDTO {
    private UUID id;
    private String trackingNumber;
    private DimensionsDTO dimensions;
    private BigDecimal weight; //cieżar
    private CoordinatesDTO coordinates;
    private Long truckID;


    public PackageDTO(DimensionsDTO dimensions) {
        this.dimensions = dimensions;
    }
}
