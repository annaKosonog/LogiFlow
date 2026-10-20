package com.io.logiflow.packageConsumer.infrastructure.dao;

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

    //Starting point of the free space
    @Column(name = "position_x", nullable = false)
    private int positionX;
    @Column(name = "position_y", nullable = false)
    private int positionY;
    @Column(name = "position_z", nullable = false)
    private int positionZ;

    //Dimensions of the free space
    @Column(name = "width", nullable = false)
    private int width;
    @Column(name = "height", nullable = false)
    private int height;
    @Column(name = "depth", nullable = false)
    private int depth;

    @ManyToOne
    @JoinColumn(name = "truck_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Truck truck;

    @Version
    private Integer version;

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
