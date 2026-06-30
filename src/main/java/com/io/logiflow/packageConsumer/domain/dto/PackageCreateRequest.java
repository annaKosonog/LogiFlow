package com.io.logiflow.packageConsumer.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PackageCreateRequest {
    private DimensionsDTO dimensions;
    private BigDecimal weight; //cieżar
    private CoordinatesDTO coordinates;
}
