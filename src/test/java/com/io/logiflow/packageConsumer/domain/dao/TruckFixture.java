package com.io.logiflow.packageConsumer.domain.dao;

import com.io.logiflow.packageConsumer.infrastructure.dao.Package;
import com.io.logiflow.packageConsumer.infrastructure.dao.Truck;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.UUID;

import static com.io.logiflow.packageConsumer.domain.dao.PackageFixture.firstBoxPackToTruck;
import static com.io.logiflow.packageConsumer.domain.dao.PackageFixture.secondBoxPackToTruck;

public class TruckFixture {

    // Domyślne wymiary, które interesują geometrię, ale nie testy wagowe
    private static final int DEFAULT_WIDTH = 250;
    private static final int DEFAULT_HEIGHT = 220;
    private static final int DEFAULT_LENGTH = 600;

    // Fabryka dla pudełek (podajesz tylko wagę)
    public static com.io.logiflow.packageConsumer.infrastructure.dao.Package createBox(String weight) {
        return new Package(DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_LENGTH, new BigDecimal(weight));
    }

    // Fabryka dla ciężarówki - podajesz tylko maksymalną ładowność (maxPayload)
    // Brak setterów, czysty konstruktor!
    public static Truck createTruckWithPayload(String maxPayload) {
        return new Truck(DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_LENGTH, new BigDecimal(maxPayload));
    }

    public static Truck createSecondTruckWithPayload(String maxPayload) {
        return new Truck(178, 190, 430, new BigDecimal(maxPayload));
    }


    // Gotowy zestaw do testów np. metody unload()
    public static Truck createLoadedTruck() {
        // Tworzymy ciężarówkę z limitem 200.00
        Truck truck = createTruckWithPayload("200.00");
        truck.setUuid(UUID.fromString("528e0004-a6f9-4018-bcba-87e20ad08606"));
        truck.setLicensePlate("WI 1234E");
        truck.getPackedPackages().add(firstBoxPackToTruck());

        // Ładujemy paczki
        truck.load(createBox("30.00"));
        truck.load(createBox("50.00"));

        return truck;
    }

        public static Truck createThirdTruck() {
            // Tworzymy ciężarówkę z limitem 200.00
            Truck truck = createTruckWithPayload("200.00");
            truck.setUuid(UUID.fromString("aaaaa-abbbb-4018-bcba-87e20ad08606"));
            truck.setLicensePlate("KR 12345");
            return truck;
    }

    public static Truck createLoadedSecondTruck() {
        // Tworzymy ciężarówkę z limitem 200.00
        Truck truck = createSecondTruckWithPayload("150.00");
        truck.setUuid(UUID.fromString("c771f079-fb74-4811-b491-e78673c41e0d"));
        truck.setLicensePlate("W0 1234");
        truck.getPackedPackages().add(secondBoxPackToTruck());

        // Ładujemy paczki
        truck.load(createBox("40.00"));
        truck.load(createBox("50.00"));
        return truck;
    }

    public static Truck createTruckWithoutCargoSpaces() {
        Truck t1 = createTruckWithPayload("200");
        t1.setUuid(UUID.fromString("7b92a341-9e12-4290-8d1e-8e3b52d19f2a"));
        t1.setLicensePlate("PO 1234A");
        t1.setCargoCompartments(new ArrayList<>());
        return t1;
    }
}
