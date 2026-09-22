package com.io.logiflow.packageConsumer.domain.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.io.logiflow.packageConsumer.domain.dto.response.CoordinatesDTO;
import com.io.logiflow.packageConsumer.domain.dto.response.DimensionsDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PackageCreateRequest {

    @NotNull(message = "Dimensions are required")
    @Valid
    private DimensionsDTO dimensions;

    @NotNull(message = "Weight is required")
    @Positive(message = "Weight must be positive")
    private BigDecimal weight; //cieżar

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private CoordinatesDTO coordinates;
}
