package com.io.logiflow.packageConsumer.infrastructure.service.parcel_preparation;

import com.io.logiflow.packageConsumer.domain.dao.CargoSpace;
import com.io.logiflow.packageConsumer.domain.dao.Package;
import com.io.logiflow.packageConsumer.domain.dao.Truck;
import com.io.logiflow.packageConsumer.domain.dto.request.ParcelInformation;
import com.io.logiflow.packageConsumer.domain.dto.response.CoordinatesDTO;
import com.io.logiflow.packageConsumer.domain.dto.response.DimensionsDTO;
import com.io.logiflow.packageConsumer.infrastructure.service.CargoGrid;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class CargoSpaceSplitter {

    private static final int MIN_ZONE_SIZE = 40; // 40 mm (4 cm)

    public Optional<CoordinatesDTO> findFreePosition(CargoGrid grid, DimensionsDTO dimensions) {
        int w = dimensions.getWidth();
        int h = dimensions.getHeight();
        int d = dimensions.getDepth_length();

        for (int z = 0; z <= grid.getDepth() - d; z++) {
            for (int y = 0; y <= grid.getHeight() - h; y++) {
                for (int x = 0; x <= grid.getWidth() - w; x++) {
                    if (grid.isFree(x, y, z, w, h, d)) {
                        return Optional.of(new CoordinatesDTO(x, y, z));
                    }
                }
            }
        }
        return Optional.empty();
    }

    public void placeParcel(CargoGrid grid, ParcelInformation parcel) {
        grid.markOccupied(parcel);
    }

    public List<CargoSpace> split(CargoSpace zone, Package aPackageToPlace, Truck truck) {

        //Dimensions Package
        int effectiveWidth = aPackageToPlace.getEffectiveWidth();
        int effectiveHeight = aPackageToPlace.getHeight();
        int effectiveDepth = aPackageToPlace.getEffectiveDepth();

        int originX = zone.getPositionX();
        int originY = zone.getPositionY();
        int originZ = zone.getPositionZ();

        int zoneWidth = zone.getWidth();
        int zoneHeight = zone.getHeight();
        int zoneDepth = zone.getDepth();
        List<CargoSpace> freeSpaceForNewPackages = new ArrayList<>(3);
        //Space - right
        if (zoneWidth - effectiveWidth >= MIN_ZONE_SIZE && zoneHeight >= MIN_ZONE_SIZE && zoneDepth >= MIN_ZONE_SIZE) {
            freeSpaceForNewPackages.add(new CargoSpace(originX + effectiveWidth, originY, originZ,
                    zoneWidth - effectiveWidth, zoneHeight, zoneDepth, truck));
        } //TOP
        if (effectiveWidth >= MIN_ZONE_SIZE && zoneHeight - effectiveHeight >= MIN_ZONE_SIZE && zoneDepth >= MIN_ZONE_SIZE) {
            freeSpaceForNewPackages.add(new CargoSpace(originX, originY + effectiveHeight, originZ,
                    effectiveWidth, zoneHeight - effectiveHeight, zoneDepth, truck));
        } //FRONT
        if (effectiveWidth >= MIN_ZONE_SIZE && zoneHeight - effectiveHeight >= MIN_ZONE_SIZE && zoneDepth >= MIN_ZONE_SIZE) {
            freeSpaceForNewPackages.add(new CargoSpace(originX, originY, originZ + effectiveDepth,
                    effectiveWidth, effectiveHeight, zoneDepth - effectiveDepth, truck));
        }
        return freeSpaceForNewPackages;
    }
}