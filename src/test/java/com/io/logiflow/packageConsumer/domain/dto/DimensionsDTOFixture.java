package com.io.logiflow.packageConsumer.domain.dto;

public class DimensionsDTOFixture {

    public static DimensionsDTO dimensionMediumDTO() {
        return new DimensionsDTO(2, 2, 2);
    }

    public static DimensionsDTO dimensionSmallDTO() {
        return new DimensionsDTO(1, 1, 1);
    }

    public static DimensionsDTO dimensionLargeDTO() {
        return new DimensionsDTO(5, 5, 5);
    }

    public static DimensionsDTO dimensionHeavyDTO() {
        return new DimensionsDTO(4, 1, 1);
    }

    public static DimensionsDTO dimensionLightDTO() {
        return new DimensionsDTO(2, 2, 1);
    }

    public static DimensionsDTO dimensionLargeSpace() {
        return new DimensionsDTO(10, 10, 10);
    }

    public static DimensionsDTO dimensionHeightDTO() {
        return new DimensionsDTO(10, 221, 10);
    }

    public static DimensionsDTO dimensionDepthDTO() {
        return new DimensionsDTO(10, 10, 601);
    }

    public static DimensionsDTO dimensionUpdate() {
        return new DimensionsDTO(45, 10, 10);
    }

    public static DimensionsDTO dimensionTruck() {
        return new DimensionsDTO(250, 220, 600);
    }


}
