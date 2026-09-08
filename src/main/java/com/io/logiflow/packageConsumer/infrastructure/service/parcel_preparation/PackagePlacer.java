package com.io.logiflow.packageConsumer.infrastructure.service.parcel_preparation;

import com.io.logiflow.packageConsumer.domain.dao.CargoSpace;
import com.io.logiflow.packageConsumer.domain.dao.Package;
import com.io.logiflow.packageConsumer.domain.dao.Truck;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PackagePlacer {

    private final PackingGeometry packingGeometry;
    private final PlacementStrategy placementStrategy;
    private final CollisionDetector collisionDetector;
    private final CargoSpaceSplitter cargoSpaceSplitter;

    public boolean packSinglePackage(Package aPackage, Truck truck, List<CargoSpace> zones, List<Package> alreadyLoaded) {
        boolean packed = tryPlacePackage(aPackage, truck, zones, alreadyLoaded);
        if (packed) {
            truck.load(aPackage);
        } else {
            resetPackageCoordinates(aPackage);
        }
        return packed;
    }

    private boolean tryPlacePackage(Package aPackage, Truck truck,
                                    List<CargoSpace> finalCargoSpaces, List<Package> alreadyLoaded) {
        for (CargoSpace cargoSpace : finalCargoSpaces) {
            List<int[]> candidates = placementStrategy.generateCandidatePoints(cargoSpace, truck.getPackedPackages());
            candidates.sort(
                    Comparator.<int[]>comparingInt(c -> c[1])   // Y — grawitacja (dół najpierw)
                            .thenComparingInt(c -> c[2])         // Z — tył
                            .thenComparingInt(c -> c[0])         // X — bok
            );

            for (int[] point : candidates) {
                int x = point[0], y = point[1], z = point[2];

                Boolean bestRotation = packingGeometry.pickBestRotation(aPackage, cargoSpace, x, y, z);
                if (bestRotation == null) {
                    continue; // żadna orientacja nie pasuje w tym punkcie
                }
                aPackage.setRotated(bestRotation);

                aPackage.setCoordX(x);
                aPackage.setCoordY(y);
                aPackage.setCoordZ(z);

                // If a package overlaps with another one, we reset its position and check the NEXT space
                if (collisionDetector.overlapsWithLoadedPackages(aPackage, alreadyLoaded)) {
                    resetPackageCoordinates(aPackage);
                    continue;
                }
                aPackage.setTruck(truck);
                alreadyLoaded.add(aPackage);
                splitAndReplaceZone(cargoSpace, aPackage, truck, finalCargoSpaces);
                return true;
            }
        }

        // No location found in any zone/orientation — clear the rotation flag along with the coordinates
        aPackage.setRotated(false);
        return false;
    }


    private void splitAndReplaceZone(CargoSpace usedZone, Package placeOfPackage, Truck truck,
                                     List<CargoSpace> zones) {
        List<CargoSpace> split = cargoSpaceSplitter.split(usedZone, placeOfPackage, truck);

        truck.getEmptyCargoSpaces().remove(usedZone);
        truck.getEmptyCargoSpaces().addAll(split);

        zones.remove(usedZone);
        zones.addAll(split);
    }


    private void resetPackageCoordinates(Package pkg) {
        pkg.setCoordX(-1);
        pkg.setCoordY(-1);
        pkg.setCoordZ(-1);
        pkg.setRotated(false);
    }
}

