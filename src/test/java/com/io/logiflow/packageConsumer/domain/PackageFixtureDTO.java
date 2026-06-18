package com.io.logiflow.packageConsumer.domain;

import com.io.logiflow.packageConsumer.domain.dto.PackageDTO;

import java.util.UUID;

public class PackageFixtureDTO {
    public static PackageDTO smallDTO() {
        return new PackageDTO(UUID.randomUUID(), "XYZ-DEFAULT", null, null, null);
    }

    public static PackageDTO largeBoxDTO() {
        return new PackageDTO(UUID.randomUUID(), "XYZ-CUSTOM", null, null, 2L);
    }


}
