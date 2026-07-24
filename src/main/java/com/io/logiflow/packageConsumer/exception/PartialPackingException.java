package com.io.logiflow.packageConsumer.exception;

import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class PartialPackingException extends RuntimeException {

    private final UUID truckId;
    private final List<UUID> unpackedPackageUuids;

    public PartialPackingException(UUID truckId, List<UUID> unpackedPackageUuids) {
        super("Truck " + truckId + ": " + unpackedPackageUuids.size()
                + " package(s) could not be placed: " + unpackedPackageUuids);
        this.truckId = truckId;
        this.unpackedPackageUuids = unpackedPackageUuids;
    }
}
