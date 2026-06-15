package com.io.logiflow.packageConsumer.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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

    @Column(name = "width_w")
    private int W; //Szerokość calkowita samochodu os X
    @Column(name = "height_h")
    private int H;  //Wysokosc całkowita samochodu os Y
    @Column(name = "length_l")
    private int L;  //Długość całkowita samochodu os Z
    @Column(name = "max_payload")
    private BigDecimal maxPayload;  //Maksymalna ladownosc samochodu
    private BigDecimal currentWeight = BigDecimal.ZERO;

    @JsonManagedReference
    @OneToMany(mappedBy = "truck")
    private List<Package> packedPackages = new ArrayList<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "truck")
    private List<CargoSpace> emptyCargoSpaces = new ArrayList<>();


    public Truck(int W, int H, int L, BigDecimal maxPayload) {
        this.W = W;
        this.H = H;
        this.L = L;
        this.maxPayload = maxPayload;
        this.emptyCargoSpaces.add(new CargoSpace(0, 0, 0, W, H, L, this));
    }

    public void load(Package b) {
        if (!canPayloadHold(b)) {
            throw new RuntimeException("Truck loading failed");
        }
        packedPackages.add(b);
        this.currentWeight = this.currentWeight.add(b.getWeight());
    }

    private boolean canPayloadHold(Package b) {
        return currentWeight.add(b.getWeight()).compareTo(maxPayload) <= 0;
    }


    public void unload(Package b) {
        if (!packedPackages.contains(b)) {
            throw new IllegalArgumentException("Cannot unload a box that is not on the truck");
        }
        this.currentWeight = this.currentWeight.subtract(b.getWeight());
        this.packedPackages.remove(b);
    }
}
