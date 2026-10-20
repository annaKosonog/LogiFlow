package com.io.logiflow.packageConsumer.domain;

import com.io.logiflow.packageConsumer.infrastructure.dto.request.ParcelInformation;
import com.io.logiflow.packageConsumer.infrastructure.dto.response.CoordinatesDTO;
import lombok.Getter;

import java.util.BitSet;
import java.util.List;

@Getter
public class CargoGrid {

    private final BitSet occupied;
    private final int width, height, depth;

    public CargoGrid(int width, int height, int depth) {
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.occupied = new BitSet(width * height * depth);
    }

    // Konstruktor do wczytania ISTNIEJĄCEGO stanu (np. z Truck.occupiedBits)
    public CargoGrid(int width, int height, int depth, BitSet existingOccupied) {
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.occupied = existingOccupied;
    }

    private int index(int x, int y, int z) {
        return x + width * (y + height * z);
    }

    // Oznaczenie zajetego miejsca
    public void markOccupied(ParcelInformation parcel) {
        CoordinatesDTO position = parcel.getPosition();
        for (int x = position.x(); x < position.x() + parcel.getDimension().getWidth(); x++) {
            for (int y = position.y(); y < position.y() + parcel.getDimension().getHeight(); y++) {
                for (int z = position.z(); z < position.z() + parcel.getDimension().getDepth_length(); z++) {
                    occupied.set(index(x, y, z));
                }
            }
        }
    }

    public boolean isFree(int x, int y, int z, int w, int h, int d) {
        if (x + w > width || y + h > height || z + d > depth) {
            return false;
        }
        // 2. Przeglądanie przestrzeni odcinek po odcinku w osi X
        for (int xi = x; xi < x + w; xi++) {
            for (int yi = y; yi < y + h; yi++) {
                for (int zi = z; zi < z + d; zi++) {
                    if (occupied.get(index(xi, yi, zi))) {
                        return false; // choć jedna komórka zajęta → brak miejsca
                    }
                }
            }
        }
        return true;
    }

    public static CargoGrid fromPlacedParcels(int truckWidth, int truckHeight, int truckDepth,
                                              List<ParcelInformation> placedParcels) {
        CargoGrid grid = new CargoGrid(truckWidth, truckHeight, truckDepth);
        placedParcels.forEach(grid::markOccupied);
        return grid;
    }
}
