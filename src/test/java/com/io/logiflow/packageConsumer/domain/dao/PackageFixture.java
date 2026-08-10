package com.io.logiflow.packageConsumer.domain.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.io.logiflow.packageConsumer.domain.dao.TruckFixture.createBox;
import static com.io.logiflow.packageConsumer.domain.dao.TruckFixture.createLoadedTruck;

public class PackageFixture {

    public static Package boxAt(int x, int y, int z, String weight) {
        Package pkg = createBox(weight); // metoda z Twojej klasy testowej pomocniczej (TestDataFactory / TestFixtures)
        pkg.setCoordX(x);
        pkg.setCoordY(y);
        pkg.setCoordZ(z);
        return pkg;
    }

    public static Package smallDao() {
        return new Package(1, 1, 1, new BigDecimal("10"));  // objętość = 1
    }

    public static Package smallDaoSaved() {
        Package p1 = smallDao();
        p1.setTruck(createLoadedTruck());
        return p1;
    }


    public static Package largeDao() {
        return new Package(5, 5, 5, new BigDecimal("30"));  // objętość = 125
    }

    public static Package mediumDao() {
        return new Package(2, 2, 2, new BigDecimal("20")); // objętość = 8
    }

    public static Package largeBox() {
        return new Package(700, 10, 10, new BigDecimal("50")); // objętość = 1000
    }

    public static Package heightBox() {
        return new Package(10, 221, 10, new BigDecimal("50")); // objętość = 1000
    }

    public static Package depthBox() {
        return new Package(10, 10, 601, new BigDecimal("50")); // objętość = 1000
    }

    public static Package lightBox() {
        return new Package(2, 2, 1, new BigDecimal("5"));
    }

    public static Package exact() {
        return new Package(10, 10, 10, new BigDecimal("5"));
    }

    public static Package heavyBox() {
        return new Package(4, 1, 1, new BigDecimal("20"));
    }

    public static Package boxThatFitsExactly(CargoSpace cargoSpace) {
        return new Package(cargoSpace.getWidth(), cargoSpace.getHeight(), cargoSpace.getDepth(), new BigDecimal("10"));
    }

    public static Package firstBoxPackToTruck() {
        Package p1 = smallDao();
        //  p1.setPackageId(1L);
        p1.setUuid(UUID.fromString("3a2b1c4d-5e6f-4a7b-8c9d-0e1f2a3b4c5d"));
        p1.setTrackingNumber("TRK2026000000001");
        p1.setCoordX(0);
        p1.setCoordY(0);
        p1.setCoordZ(0);
        return p1;
    }

    public static Package secondBoxPackToTruck() {
        Package p1 = largeDao();
        //  p1.setPackageId(2L);
        p1.setUuid(UUID.fromString("9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d"));
        p1.setTrackingNumber("TRK2026000000002");
        return p1;
    }

    public static Package thirdBoxPackToTruck() {
        Package p1 = mediumDao();
        //  p1.setPackageId(2L);
        p1.setUuid(UUID.fromString("f7f4b7d2-660c-4430-bcec-ce3f9dd32c36"));
        p1.setTrackingNumber("TRK2026000000009");
        p1.setCoordX(0);
        p1.setCoordY(0);
        p1.setCoordZ(0);
        p1.setTruck(null);
        return p1;
    }


    public static Package filterPack() {
        Package p1 = largeDao();
        p1.setPackageId(2L);
        p1.setTrackingNumber("TRK2026000000002");
        return p1;
    }


    public static Package updatePackage() {
        Package updatedPackage = new Package();
        updatedPackage.setPackageId(5L);
        updatedPackage.setTrackingNumber("TRK-2026-ORIGINAL"); // Numer bez zmian!
        updatedPackage.setWidth(45);                          // Zmieniona szerokość
        updatedPackage.setHeight(10);                          // Bez zmian
        updatedPackage.setDepth(10);                          // Bez zmian
        updatedPackage.setWeight(new BigDecimal("12.95"));  //Zmieniona waga
        return updatedPackage;
    }

    public static List<Package> sortedPackages() {
        List<Package> localPackages = new ArrayList<>();
        Package small = smallDao();
        small.setTrackingNumber("XYZ-SMALL");
        Package medium = mediumDao();
        medium.setTrackingNumber("XYZ-MEDIUM");
        Package large = largeDao();
        large.setTrackingNumber("XYZ-LARGE");
        localPackages.add(large);
        localPackages.add(small);
        localPackages.add(medium);
        return localPackages;
    }

    public static List<Package> sortedPackagesDaoByWeightDescending() {
        List<Package> localPackages = new ArrayList<>();
        Package heavyPackage = heavyBox();
        heavyPackage.setTrackingNumber("XYZ-HEAVY");
        localPackages.add(heavyPackage);

        Package lightPackage = lightBox();
        lightBox().setTrackingNumber("XYZ-LIGHT");
        localPackages.add(lightPackage);
        return localPackages;
    }

    public static Package createOverSizeedPackage() {
        Package p1 = new Package();
        p1.setUuid(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6"));
        p1.setTrackingNumber("TRK2026000000123");
        p1.setWeight(new BigDecimal("10"));
        p1.setWidth(999);
        p1.setHeight(999);
        p1.setDepth(999);
        return p1;
    }
}
