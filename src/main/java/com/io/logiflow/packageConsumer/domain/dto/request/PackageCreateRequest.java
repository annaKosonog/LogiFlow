package com.io.logiflow.packageConsumer.domain.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.io.logiflow.packageConsumer.domain.dto.response.CoordinatesDTO;
import com.io.logiflow.packageConsumer.domain.dto.response.DimensionsDTO;
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

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private CoordinatesDTO coordinates;
}
