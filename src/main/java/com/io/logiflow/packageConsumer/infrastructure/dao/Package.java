package com.io.logiflow.packageConsumer.infrastructure.dao;


import jakarta.persistence.*;
import lombok.*;


import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Entity
@Table(name = "packages")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Package {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long packageId;

    @Column(unique = true, nullable = false)
    private UUID uuid = UUID.randomUUID();

    @Column(name = "tracking_number", unique = true, nullable = false, updatable = false)
    private String trackingNumber;

    //package exteriorDimensions
    private int width; //szerokośc
    private int height; //wysokość
    private int depth; //głębokość
    private BigDecimal weight; //cieżar

    //package coordinate
    @Column(name = "coord_x", nullable = false)
    private int coordX;
    @Column(name = "coord_y", nullable = false)
    private int coordY;
    @Column(name = "coord_z", nullable = false)
    private int coordZ;

    @ManyToOne
    @JoinColumn(name = "truck_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Truck truck;

    @Column(name = "rotated", nullable = false)
    private boolean rotated;

    @Column(name = "packing_order")
    private int packingOrder; //kolejność pakowania

    @Version
    private Integer version;

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