package com.io.logiflow.packageConsumer.domain;

import java.math.BigDecimal;

public class BoxFixture {

    public static Package small() {
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


}
