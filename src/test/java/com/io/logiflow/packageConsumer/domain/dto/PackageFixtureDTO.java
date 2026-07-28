package com.io.logiflow.packageConsumer.domain.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.io.logiflow.packageConsumer.domain.dto.CoordinatesDTOFixture.firstCoordinate;
import static com.io.logiflow.packageConsumer.domain.dto.DimensionsDTOFixture.*;

public class PackageFixtureDTO {

    public static PackageDTO createBoxDTO(String weight) {
        PackageDTO dto = new PackageDTO();
        dto.setTrackingNumber("XYZ-SMALL");
        dto.setDimensions(dimensionMediumDTO());
        dto.setWeight(new BigDecimal(weight)); // Jeśli w DTO masz double, lub new BigDecimal(weight)
        dto.setCoordinates(firstCoordinate());
        return dto;
    }

    public static PackageDTO smallDTO() {
        return new PackageDTO(UUID.randomUUID(), "XYZ-SMALL", dimensionSmallDTO(), new BigDecimal("10.0"), null, null);
    }

    public static PackageDTO mediumDTO() {
        return new PackageDTO((UUID.randomUUID()), "XYZ-MEDIUM", dimensionMediumDTO(), new BigDecimal("20.0"), null, null); // objętość = 8
    }

    public static PackageDTO largeBoxDTO() {
        return new PackageDTO(UUID.randomUUID(), "XYZ-LARGE", dimensionLargeDTO(), new BigDecimal("30.0"), null, null);
    }

    public static PackageDTO largeAndHeavyBoxDTO() {
        return new PackageDTO(UUID.randomUUID(), "XYZ-CUSTOM", null, new BigDecimal("130.0"), null, 2L);
    }

    public static PackageDTO heightBoxDTO() {
        return new PackageDTO(UUID.randomUUID(), "XYZ-HEIGHT", dimensionHeightDTO(), new BigDecimal("50"), null, null);
    }

    public static PackageDTO lightBoxDTO() {
        return new PackageDTO(UUID.randomUUID(), "XYZ-LIGHT", dimensionLightDTO(), new BigDecimal("5"), null, null);
    }

    public static PackageDTO heavyBoxDTO() {
        return new PackageDTO(UUID.randomUUID(), "XYZ-HEAVY", dimensionHeavyDTO(), new BigDecimal("20"), null, null);
    }

    public static PackageDTO depthBoxDTO() {
        return new PackageDTO(UUID.randomUUID(), "XYZ-DEEPTH", dimensionDepthDTO(), new BigDecimal("50"), null, null);
    }

    public static PackageDTO firstBoxPackDto() {
        return new PackageDTO(UUID.randomUUID(), "TRK-2026-A001", null, new BigDecimal("25.0"), firstCoordinate(), null);
    }

    public static PackageDTO secondBoxPackDto() {
        return new PackageDTO(UUID.randomUUID(), "TRK-2026-A002", null, new BigDecimal("50.0"), firstCoordinate(), null);
    }

    public static PackageDTO filterBoxPackDto() {
        return new PackageDTO(UUID.fromString("9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d"), "TRK2026000000002", dimensionLargeDAO(), new BigDecimal("50.0"), firstCoordinate(), null);
    }


    public static PackageDTO boxThatFitsExactlyDto() {
        return new PackageDTO(UUID.randomUUID(), "TRK-2026-A001", null, new BigDecimal("20"), firstCoordinate(), null);
    }

    public static PackageDTO updatePackageDTO() {
        PackageDTO updatedPackage = new PackageDTO();
        updatedPackage.setTrackingNumber("TRK-2026-ORIGINAL"); // Numer bez zmian!
        updatedPackage.setDimensions(dimensionUpdate());                          // Bez zmian
        updatedPackage.setWeight(new BigDecimal("12.95"));
        return updatedPackage;
    }

    public static List<PackageDTO> sortedPackagesDTO() {
        List<PackageDTO> localPackages = new ArrayList<>();
        PackageDTO large = largeBoxDTO();
        PackageDTO small = smallDTO();
        PackageDTO medium = mediumDTO();
        localPackages.add(large);
        localPackages.add(small);
        localPackages.add(medium);
        return localPackages;
    }

    public static List<PackageDTO> sortedPackagesDTOByWeightDescending() {
        List<PackageDTO> localPackages = new ArrayList<>();
        PackageDTO lightPackage = lightBoxDTO();
        PackageDTO heavyPackage = heavyBoxDTO();
        localPackages.add(heavyPackage);
        localPackages.add(lightPackage);
        return localPackages;
    }

    public static PackageDTO firstPackageToPack() {
        return new PackageDTO(UUID.fromString("7c9e0d12-5b1d-407e-8c43-1bc2d3f4a5b6"), "TRK2026000000004",
                new DimensionsDTO(100, 80, 120), new BigDecimal("45"),
                new CoordinatesDTO(0, 0, 0), null);
    }

    public static PackageDTO secondPackageToPack() {
        return new PackageDTO(UUID.fromString("b17a8d83-78b0-4875-ae78-c83d14e17312"), "TRK2026000000005",
                new DimensionsDTO(14, 146, 15), new BigDecimal("120"),
                new CoordinatesDTO(0, 0, 0), null);
    }

    public static PackageDTO thirdPackageToPack() {
        return new PackageDTO(UUID.fromString("e6d49a74-99f7-4e85-b325-3e2cd906d176"), "TRK2026000000007",
                new DimensionsDTO(50, 30, 40), new BigDecimal("15"),
                new CoordinatesDTO(0, 0, 0), null);
    }


}
