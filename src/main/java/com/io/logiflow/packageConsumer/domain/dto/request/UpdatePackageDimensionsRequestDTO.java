package com.io.logiflow.packageConsumer.domain.dto.request;

import java.math.BigDecimal;

public record UpdatePackageDimensionsRequestDTO(

        BigDecimal weight,

        Integer length,

        Integer width,

        Integer height

) {
}
