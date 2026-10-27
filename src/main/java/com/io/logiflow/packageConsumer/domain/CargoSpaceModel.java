package com.io.logiflow.packageConsumer.domain;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CargoSpaceModel {

    private Long cargoSpaceId;
    private UUID uuid = UUID.randomUUID();
    private int positionX;
    private int positionY;
    private int positionZ;
    private int width;
    private int height;
    private int depth;
    private TruckModel truck;
    private Integer version;

    public CargoSpaceModel() {
    }

    public CargoSpaceModel(int positionX, int positionY, int positionZ, int width, int height, int depth) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.positionZ = positionZ;
        this.width = width;
        this.height = height;
        this.depth = depth;
    }

    public CargoSpaceModel(int positionX, int positionY, int positionZ, int width, int height, int depth, TruckModel truck) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.positionZ = positionZ;
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.truck = truck;
    }

    public CargoSpaceModel(int width, int height, int depth) {
        this.width = width;
        this.height = height;
        this.depth = depth;
    }

}
