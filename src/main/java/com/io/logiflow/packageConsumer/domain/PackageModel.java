package com.io.logiflow.packageConsumer.domain;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Getter
@Setter
public class PackageModel {
    private Long packageId;
    private UUID uuid = UUID.randomUUID();
    private String trackingNumber;
    private int width; //szerokośc
    private int height; //wysokość
    private int depth; //głębokość
    private BigDecimal weight; //cieżar
    private int coordX;
    private int coordY;
    private int coordZ;
    private TruckModel truck;
    private boolean rotated;
    private int packingOrder; //kolejność pakowania
    private Integer version;

    public PackageModel(int width, int height, int depth, BigDecimal weight) {
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.weight = weight;
    }

    public PackageModel(Long packageId, long nextSequenceValue, int width, int height, int depth, BigDecimal weight, TruckModel truck, int coordX, int coordY, int coordZ) {
        this.packageId = packageId;
        this.trackingNumber = generateTrackingNumberAutomatically(nextSequenceValue);
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.weight = weight;
        this.coordX = coordX;
        this.coordY = coordY;
        this.coordZ = coordZ;
        this.truck = truck;
    }

    public String generateTrackingNumberAutomatically(long nextSequenceValue) {
        if (this.trackingNumber != null) {
            return this.trackingNumber;
        }
        Instant createdAt = Instant.now();
        String year = DateTimeFormatter.ofPattern("yyyy")
                .withZone(ZoneId.systemDefault()).format(createdAt);
        String paddedSequence = String.format("%09d", nextSequenceValue);
        return "TRK" + year + paddedSequence;
    }

    public int getEffectiveWidth() {
        return rotated ? this.depth : this.width;
    }

    public int getEffectiveDepth() {
        return rotated ? this.width : this.depth;
    }
}
