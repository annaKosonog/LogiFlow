package com.io.logiflow.packageConsumer.domain.dto.response;

import lombok.*;

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
