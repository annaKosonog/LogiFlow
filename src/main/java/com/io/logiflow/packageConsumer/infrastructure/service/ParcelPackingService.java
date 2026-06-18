package com.io.logiflow.packageConsumer.infrastructure.service;

import com.io.logiflow.packageConsumer.domain.dao.CargoSpace;
import com.io.logiflow.packageConsumer.domain.dao.Package;
import com.io.logiflow.packageConsumer.domain.dao.Truck;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class ParcelPackingService {

    public void pack(List<Package> boxesToPack, Truck truck) {
        sortedPack(boxesToPack);
        List<CargoSpace> emptyCargoSpaces = truck.getEmptyCargoSpaces();

        for (Package aPackage : boxesToPack) {
            //Sprawdzamy wagę
            // BRAMKARZ: Jeśli ciężarówka nie udźwignie tej paczki, odrzucamy ją OD RAZU!
            truck.load(aPackage); // Próba sprawdzenia wagi

            boolean packed = false;

            for (int i = 0; i < emptyCargoSpaces.size(); i++) {
                CargoSpace cargoSpace = emptyCargoSpaces.get(i);
                if (checksThatThePackageFits(aPackage, cargoSpace)) {
                    aPackage.placeInSpace(cargoSpace); //Paczka trafia do cieżarówki i następuje przypisanie współrzędnych paczce
                    splitSpace(cargoSpace, aPackage, emptyCargoSpaces);
                    packed = true;
                    break;
                }
            }
            if (!packed) {
                truck.unload(aPackage);
            }
        }
    }


    //Sortowanie paczek od najmniejszej do największej a potem bierzemy cięższe paczki
    private void sortedPack(List<Package> boxesToPack) {
        boxesToPack.sort(
                Comparator.comparingInt((Package b) -> b.getWidth() * b.getHeight() * b.getDepth())
                        .thenComparing(Package::getWeight, Comparator.reverseOrder())
        );
    }

    private boolean checksThatThePackageFits(Package aPackage, CargoSpace cargoSpace) {
        return aPackage.getWidth() <= cargoSpace.getWidth() &&
                aPackage.getHeight() <= cargoSpace.getHeight() &&
                aPackage.getDepth() <= cargoSpace.getDepth();
    }

    private void splitSpace(CargoSpace s, Package b, List<CargoSpace> cargoSpaces) { // podział przestrzeni (pusta - pełna = nowa)
        cargoSpaces.remove(s); // Usuniecie starego pudła

        // Generujemy 3 nowe przestrzenie, które powstały wokół paczki wewnątrz starego bloku 's'
        // 1. Przestrzeń OBOK paczki (po szerokości, idziemy po X)
        if (s.getWidth() - b.getWidth() > 0) {
            cargoSpaces.add(0, new CargoSpace(s.getPositionX() + b.getWidth(), s.getPositionY(), s.getPositionZ(), s.getWidth() - b.getWidth(), s.getHeight(), s.getDepth()));
        }
        // 2. Przestrzeń NAD paczką (po wysokości)
        if (s.getHeight() - b.getHeight() > 0) {
            cargoSpaces.add(0, new CargoSpace(s.getPositionX(), s.getPositionY() + b.getHeight(), s.getPositionZ(), s.getWidth(), s.getHeight() - b.getHeight(), s.getDepth()));
        }
        // 3. Przestrzeń PRZED paczką (po długości/głębokości)
        if (s.getDepth() - b.getDepth() > 0) {
            cargoSpaces.add(0, new CargoSpace(s.getPositionX(), s.getPositionY(), s.getPositionZ() + b.getDepth(), s.getWidth(), s.getHeight(), s.getDepth() - b.getDepth()));
        }

    }
}
