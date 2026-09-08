package com.io.logiflow.packageConsumer.infrastructure.service.parcel_preparation;

import com.io.logiflow.packageConsumer.domain.dao.CargoSpace;
import com.io.logiflow.packageConsumer.domain.dao.Package;
import com.io.logiflow.packageConsumer.domain.dao.Truck;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CargoSpaceSplitter {

    private static final int MIN_ZONE_SIZE = 40; // 40 mm (4 cm)


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
        if (zoneWidth - effectiveWidth >= MIN_ZONE_SIZE && zoneHeight >=MIN_ZONE_SIZE && zoneDepth >=MIN_ZONE_SIZE) {
            freeSpaceForNewPackages.add(new CargoSpace(originX + effectiveWidth, originY, originZ,
                    zoneWidth - effectiveWidth, zoneHeight, zoneDepth, truck));
        } //TOP
        if (effectiveWidth >= MIN_ZONE_SIZE && zoneHeight - effectiveHeight >= MIN_ZONE_SIZE && zoneDepth >= MIN_ZONE_SIZE) {
            freeSpaceForNewPackages.add(new CargoSpace(originX, originY + effectiveHeight, originZ,
                    effectiveWidth, zoneHeight - effectiveHeight, zoneDepth, truck));
        } //FRONT
        if (effectiveWidth >= MIN_ZONE_SIZE && zoneHeight - effectiveHeight >= MIN_ZONE_SIZE && zoneDepth >= MIN_ZONE_SIZE) {
            freeSpaceForNewPackages.add(new CargoSpace(originX, originY, originZ + effectiveDepth,
                    effectiveWidth,  effectiveHeight, zoneDepth-effectiveDepth, truck));
        }
        return freeSpaceForNewPackages;
    }
}