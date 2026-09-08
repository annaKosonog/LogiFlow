package com.io.logiflow.packageConsumer.infrastructure.service.parcel_preparation;


import com.io.logiflow.packageConsumer.domain.dao.CargoSpace;
import com.io.logiflow.packageConsumer.domain.dao.Truck;
import com.io.logiflow.packageConsumer.exception.ResourceAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class CargoSpaceFilter {

    private static final Logger log = LoggerFactory.getLogger(CargoSpaceFilter.class);

    public List<CargoSpace> filterValidZones(Truck truck) {
        List<CargoSpace> allZones = truck.getEmptyCargoSpaces();
        log.debug("Liczba stref przed filtrem: {}", allZones.size());

        List<CargoSpace> finalCargoSpaces = new ArrayList<>(allZones
                .stream()
                .filter(zone -> zoneFitsWithinTruck(zone, truck))
                .toList());

        if (finalCargoSpaces.isEmpty()) {
            throw new ResourceAlreadyExistsException("Truck ID: " + truck.getTruckId() + " has no valid available CargoSpaces!");
        }
        return finalCargoSpaces;
    }


    private boolean zoneFitsWithinTruck(CargoSpace zone, Truck truck) {
        boolean fits = zone.getPositionX() >= 0 && zone.getPositionY() >= 0 && zone.getPositionZ() >= 0
                && zone.getPositionX() + zone.getWidth() <= truck.getWidth()
                && zone.getPositionY() + zone.getHeight() <= truck.getHeight()
                && zone.getPositionZ() + zone.getDepth() <= truck.getLength();
        if (!fits) {
            log.debug("ZONE REJECTED! Zone (WxHxD): {}x{}x{} vs Truck (WxHxL): {}x{}x{}",
                    zone.getWidth(), zone.getHeight(), zone.getDepth(),
                    truck.getWidth(), truck.getHeight(), truck.getLength());
        }
        return fits;
    }
}
