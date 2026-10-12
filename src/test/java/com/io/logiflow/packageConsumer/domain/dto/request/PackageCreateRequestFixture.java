package com.io.logiflow.packageConsumer.domain.dto.request;

import com.io.logiflow.packageConsumer.domain.dto.response.DimensionsDTO;

import java.math.BigDecimal;

import static com.io.logiflow.packageConsumer.domain.dto.response.DimensionsDTOFixture.*;

public class PackageCreateRequestFixture {

    public static PackageCreateRequest firstBoxCreateRequest() {
        return new PackageCreateRequest(dimensionMediumDTO(), new BigDecimal("25.0"), null);
    }

    public static PackageCreateRequest createPackageWithoutDimension() {
        return new PackageCreateRequest(new DimensionsDTO(), new BigDecimal("25.0"), null);
    }

    public static PackageCreateRequest largeAndHeavyBoxRequest() {
        return new PackageCreateRequest(dimensionLargeDTO(), new BigDecimal("130.0"), null);
    }

    public static PackageCreateRequest smallRequest() {
        return new PackageCreateRequest(dimensionSmallDTO(), new BigDecimal("10.0"), null);
    }

    public static PackageCreateRequest exactRequest() {
        return new PackageCreateRequest(new DimensionsDTO(10, 10, 10), new BigDecimal("5.0"), null);
    }

    public static PackageCreateRequest exactSecondRequest() {
        return new PackageCreateRequest(new DimensionsDTO(210, 90, 40), new BigDecimal("54.0"), null);
    }

    public static PackageCreateRequest exactThirdRequest() {
        return new PackageCreateRequest(new DimensionsDTO(67, 54, 600), new BigDecimal("123.0"), null);
    }

    public static PackageCreateRequest requestFirst() {
        return new PackageCreateRequest(new DimensionsDTO(5, 5, 5), new BigDecimal("1.0"), null);
    }

    public static PackageCreateRequest requestSecond() {
        return new PackageCreateRequest(new DimensionsDTO(6, 6, 6), new BigDecimal("2.0"), null);
    }


}
