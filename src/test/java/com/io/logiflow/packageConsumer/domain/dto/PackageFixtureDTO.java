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
        return new PackageDTO(UUID.randomUUID(), "XYZ-LARGE", dimensionLargeDTO(), new BigDecimal("30.0"), null, 2L);
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
        return new PackageDTO(UUID.randomUUID(), "TRK2026000000002", null, new BigDecimal("50.0"), firstCoordinate(), null);
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


}
