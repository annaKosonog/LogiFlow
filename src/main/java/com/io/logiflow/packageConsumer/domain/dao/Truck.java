package com.io.logiflow.packageConsumer.domain.dao;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "trucks")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Truck {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "truck_id")
    private Long truckId;

    @Column(unique = true, nullable = false)
    private UUID uuid = UUID.randomUUID();

    @Column(name = "width_w")
    private int width; //Szerokość calkowita samochodu os X
    @Column(name = "height_h")
    private int height;  //Wysokosc całkowita samochodu os Y
    @Column(name = "length_l")
    private int length;  //Długość całkowita samochodu os Z
    @Column(name = "max_payload")
    private BigDecimal maxPayload;  //Maksymalna ladownosc samochodu
    private BigDecimal currentWeight = BigDecimal.ZERO;

    @JsonManagedReference
    @OneToMany(mappedBy = "truck")
    private List<Package> packedPackages = new ArrayList<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "truck")
    private List<CargoSpace> emptyCargoSpaces = new ArrayList<>();

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
}
