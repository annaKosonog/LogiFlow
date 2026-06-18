package com.io.logiflow.packageConsumer.domain.dao;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "packages")
@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public class Package {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long packageId;

    @Column(unique = true, nullable = false)
    private UUID uuid = UUID.randomUUID();

    @NotNull
    private String trackingNumber;

    //wymiary paczki
    private int width; //szerokośc
    private int height; //wysokość
    private int depth; //głębokość
    private BigDecimal weight; //cieżar


    //współrzedne paczki
    @Column(name = "coord_x", nullable = false)
    private int x;
    @Column(name = "coord_y", nullable = false)
    private int y;
    @Column(name = "coord_z", nullable = false)
    private int z;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "truck_id") // Wskazuje nazwę kolumny z kluczem obcym w bazie SQL
    private Truck truck;


    public Package(int width, int height, int depth, BigDecimal weight) {
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.weight = weight;
    }

    public Package(Long packageId, String trackingNumber, int width, int height, int depth, BigDecimal weight, Truck truck, int x, int y, int z) {
        this.packageId = packageId;
        this.trackingNumber = trackingNumber;
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.weight = weight;
        this.x = x;
        this.y = y;
        this.z = z;
        this.truck = truck;
    }

    public void placeInSpace(CargoSpace cargoSpace) {
        this.x = cargoSpace.getPositionX();
        this.y = cargoSpace.getPositionY();
        this.z = cargoSpace.getPositionZ();
    }
}

//todo zrobić warstwe dto