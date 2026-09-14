package com.io.logiflow.packageConsumer.domain.dao;

import com.io.logiflow.packageConsumer.infrastructure.service.CargoGrid;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "trucks")
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Truck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "truck_id")
    private Long truckId;

    @Column(unique = true, nullable = false)
    private UUID uuid = UUID.randomUUID();

    @Column(name = "license_plate", nullable = false, unique = true)
    private String licensePlate;

    @Column(name = "width_w")
    private int width;
    @Column(name = "height_h")
    private int height;
    @Column(name = "length_l")
    private int length;
    @Column(name = "max_payload")
    private BigDecimal maxPayload;
    private BigDecimal currentWeight = BigDecimal.ZERO;

    @OneToMany(mappedBy = "truck")
    private List<Package> packedPackages = new ArrayList<>();

    @OneToMany(mappedBy = "truck", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CargoSpace> emptyCargoSpaces = new ArrayList<>();

    @Version
    private Integer version;

    @Lob
    private byte[] occupiedBits;

    public Truck(int width, int height, int length, BigDecimal maxPayload) {
        this.width = width;
        this.height = height;
        this.length = length;
        this.maxPayload = maxPayload;
        this.emptyCargoSpaces.add(new CargoSpace(0, 0, 0, width, height, length, this));
    }

    public void load(Package b) {
        if (!canPayloadHold(b)) {
            throw new RuntimeException("Truck loading failed");
        }
        packedPackages.add(b);
        this.currentWeight = this.currentWeight.add(b.getWeight());
    }

    private boolean canPayloadHold(Package b) {
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

    public CargoGrid toCargoGrid() {
        return new CargoGrid(width, height, length, BitSet.valueOf(occupiedBits));
    }

    public void updateOccupiedBits(CargoGrid grid) {
        this.occupiedBits = grid.getOccupied().toByteArray();
    }
}
