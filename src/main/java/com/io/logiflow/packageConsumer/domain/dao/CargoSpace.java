package com.io.logiflow.packageConsumer.domain.dao;


import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.UUID;

@Entity
@Table(name = "cargo_spaces")
@Data
@ToString
public class CargoSpace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cargo_space_id")
    private Long cargoSpaceId;

    @Column(unique = true, nullable = false)
    private UUID uuid = UUID.randomUUID();

    //Punkt początkowy wolnej przestrzeni
    @Column(name = "position_x", nullable = false)
    private int positionX;
    @Column(name = "position_y", nullable = false)
    private int positionY;
    @Column(name = "position_z", nullable = false)
    private int positionZ;

    //Wymiary wolnej przestrzeni
    @Column(name = "width", nullable = false)
    private int width; //Szerokość
    @Column(name = "height", nullable = false)
    private int height; //Wysokosc
    @Column(name = "depth", nullable = false)
    private int depth; //Glebokosc

    @ManyToOne
    @JoinColumn(name = "truck_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Truck truck;

    // Konstruktor wymagany przez JPA
    public CargoSpace() {
    }

    public CargoSpace(int positionX, int positionY, int positionZ, int width, int height, int depth) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.positionZ = positionZ;
        this.width = width;
        this.height = height;
        this.depth = depth;
    }

    // Konstruktor biznesowy do tworzenia nowej przestrzeni
    public CargoSpace(int positionX, int positionY, int positionZ, int width, int height, int depth, Truck truck) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.positionZ = positionZ;
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.truck = truck;
    }

    public CargoSpace(int width, int height, int depth) {
        this.width = width;
        this.height = height;
        this.depth = depth;
    }
}
