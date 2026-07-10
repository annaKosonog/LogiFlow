package com.io.logiflow.packageConsumer.domain.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class DimensionsDTO {
    private int width; //szerokośc
    private int height; //wysokość
    private int depth_length; //głębokość, długość
}
