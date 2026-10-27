package com.io.logiflow.packageConsumer.domain;


import com.io.logiflow.packageConsumer.infrastructure.dao.Package;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class TruckModel {

    private Long truckId;
    private UUID uuid = UUID.randomUUID();
    private String licensePlate;
    private int width;
    private int height;
    private int length;
    private BigDecimal maxPayload;
    private BigDecimal currentWeight = BigDecimal.ZERO;
    private List<PackageModel> packedPackages = new ArrayList<>();
    private List<CargoSpaceModel> cargoCompartments = new ArrayList<>();

    private Integer version;

    public TruckModel(int width, int height, int length, BigDecimal maxPayload) {
        this.width = width;
        this.height = height;
        this.length = length;
        this.maxPayload = maxPayload;
        this.cargoCompartments.add(new CargoSpaceModel(0, 0, 0, width, height, length, this));
    }

    public void load(PackageModel b) {
        if (!canPayloadHold(b)) {
            throw new RuntimeException("Truck loading failed");
        }
        packedPackages.add(b);
        this.currentWeight = this.currentWeight.add(b.getWeight());
    }

    private boolean canPayloadHold(PackageModel b) {
        BigDecimal newTotalWeight = this.currentWeight.add(b.getWeight());
        return newTotalWeight.compareTo(this.maxPayload) <= 0;
    }

    public void unload(Package b) {
        if (!packedPackages.contains(b)) {
            throw new IllegalArgumentException("Cannot unload a box that is not on the truck");
        }
        this.currentWeight = this.currentWeight.subtract(b.getWeight());
        this.packedPackages.remove(b);
    }

}
