package com.io.logiflow.packageConsumer.domain.dao;

import java.math.BigDecimal;

public class PackageFixture {

    public static Package smallDao() {
        return new Package(1, 1, 1, new BigDecimal("10"));  // objętość = 1
    }

    public static Package large() {
        return new Package(5, 5, 5, new BigDecimal("10"));  // objętość = 125
    }

    public static Package medium() {
        return new Package(2, 2, 2, new BigDecimal("10")); // objętość = 8
    }

    public static Package largeBox() {
        return new Package(251, 10, 10, new BigDecimal("50")); // objętość = 1000
    }

    public static Package heightBox() {
        return new Package(10, 221, 10, new BigDecimal("50")); // objętość = 1000
    }

    public static Package deepthBox() {
        return new Package(10, 10, 601, new BigDecimal("50")); // objętość = 1000
    }

    public static Package lightBox() {
        return new Package(2, 2, 2, new BigDecimal("5"));
    }

    public static Package heavyBox() {
        return new Package(2, 2, 2, new BigDecimal("20"));
    }

    public static Package boxThatFitsExactly(CargoSpace cargoSpace) {
        return new Package(cargoSpace.getWidth(), cargoSpace.getHeight(), cargoSpace.getDepth(), new BigDecimal("10"));
    }

    public static Package firstBoxPackToTruck() {
        Package p1 = smallDao();
        p1.setPackageId(1L);
        p1.setTrackingNumber("TRK-2026-A001");
        return p1;
    }

    public static Package secondBoxPackToTruck() {
        Package p1 = large();
        p1.setPackageId(2L);
        p1.setTrackingNumber("TRK-2026-A002");
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


}
