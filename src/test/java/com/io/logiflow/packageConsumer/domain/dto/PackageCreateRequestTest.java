package com.io.logiflow.packageConsumer.domain.dto;

import java.math.BigDecimal;

import static com.io.logiflow.packageConsumer.domain.dto.DimensionsDTOFixture.dimensionSmallDTO;

public class PackageCreateRequestTest {

    public static PackageCreateRequest firstBoxCreateRequest() {
        return new PackageCreateRequest(null, new BigDecimal("25.0"), null);
    }

    public static PackageCreateRequest largeAndHeavyBoxRequest() {
        return new PackageCreateRequest(null, new BigDecimal("130.0"), null);
    }

    public static PackageCreateRequest smallRequest() {
        return new PackageCreateRequest(dimensionSmallDTO(), new BigDecimal("10.0"), null);
    }
}
