package com.io.logiflow.packageConsumer.domain.dao;

public class CargoSpaceFixture {
    public static CargoSpace smallSpace() {
        return new CargoSpace(0, 0, 0, 0, 2, 2);  // pozycja (0,0,0), wymiary 2x2x2
    }

    public static CargoSpace mediumSpace() {
        return new CargoSpace(0, 0, 0, 5, 5, 5);  // pozycja (0,0,0), wymiary 5x5x5
    }

    public static CargoSpace largeSpace() {
        return new CargoSpace(0, 0, 0, 10, 10, 10); // pozycja (0,0,0), wymiary 10x10x10
    }

    public static CargoSpace spaceAtPosition() {
        return new CargoSpace(1, 2, 3);  // dowolna pozycja i wymiary
    }
}
