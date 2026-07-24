package com.io.logiflow.packageConsumer.domain.dao;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Entity
@Table(name = "packages")
@Data
@NoArgsConstructor
public class Package {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long packageId;

    @Column(unique = true, nullable = false)
    private UUID uuid = UUID.randomUUID();

    @NotNull
    @Column(name = "tracking_number", unique = true, nullable = false, updatable = false)
    private String trackingNumber;

    //wymiary paczki
    private int width; //szerokośc
    private int height; //wysokość
    private int depth; //głębokość
    private BigDecimal weight; //cieżar

    //współrzedne paczki
    @Column(name = "coord_x", nullable = false)
    private int coordX;
    @Column(name = "coord_y", nullable = false)
    private int coordY;
    @Column(name = "coord_z", nullable = false)
    private int coordZ;

    @ManyToOne
    @JoinColumn(name = "truck_id") // Wskazuje nazwę kolumny z kluczem obcym w bazie SQL
    private Truck truck;

    @Column(name = "rotated", nullable = false)
    private boolean rotated;

    public Package(int width, int height, int depth, BigDecimal weight) {
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.weight = weight;
    }

    public Package(Long packageId, long nextSequenceValue, int width, int height, int depth, BigDecimal weight, Truck truck, int coordX, int coordY, int coordZ) {
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
        LocalDateTime createdAt = LocalDateTime.now();
        String year = createdAt.format(DateTimeFormatter.ofPattern("yyyy"));
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