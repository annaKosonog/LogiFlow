package com.io.logiflow.packageConsumer.domain.dto.response;

import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class DimensionsDTO {
    @Positive(message = "Width must be positive")
    private int width;

    @Positive(message = "Height must be positive")
    private int height;

    @Positive(message = "Depth must be positive")
    private int depth_length;
}
