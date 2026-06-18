package com.io.logiflow.packageConsumer.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DimensionsDTO {
    private int width; //szerokośc
    private int height; //wysokość
    private int depth; //głębokość
    private BigDecimal weight; //cieżar
}
