package com.io.logiflow.packageConsumer.domain.dao;

import java.math.BigDecimal;

public class TruckFixture {

    // Domyślne wymiary, które interesują geometrię, ale nie testy wagowe
    private static final int DEFAULT_WIDTH = 250;
    private static final int DEFAULT_HEIGHT = 220;
    private static final int DEFAULT_LENGTH = 600;

    // Fabryka dla pudełek (podajesz tylko wagę)
    public static Package createBox(String weight) {
        return new Package(DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_LENGTH, new BigDecimal(weight));
    }

    // Fabryka dla ciężarówki - podajesz tylko maksymalną ładowność (maxPayload)
    // Brak setterów, czysty konstruktor!
    public static Truck createTruckWithPayload(String maxPayload) {
        return new Truck(DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_LENGTH, new BigDecimal(maxPayload));
    }

    // Gotowy zestaw do testów np. metody unload()
    public static Truck createLoadedTruck() {
        // Tworzymy ciężarówkę z limitem 200.00
        Truck truck = createTruckWithPayload("200.00");
        truck.setTruckId(1L);

        // Ładujemy paczki
        truck.load(createBox("30.00"));
        truck.load(createBox("50.00"));

        return truck;
    }
}
