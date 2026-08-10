package com.io.logiflow.packageConsumer.infrastructure.service.units;

import com.io.logiflow.packageConsumer.domain.dao.Package;
import com.io.logiflow.packageConsumer.domain.dao.Truck;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.io.logiflow.packageConsumer.domain.dao.TruckFixture.createBox;
import static com.io.logiflow.packageConsumer.domain.dao.TruckFixture.createLoadedTruck;
import static org.junit.jupiter.api.Assertions.*;

class TruckTest {

    private Truck truck;

    @BeforeAll
    static void setUpAll() {
        System.out.println(">> Inicjalizacja zasobów globalnych");
    }

    @BeforeEach
    void setUp() {
        truck = createLoadedTruck();
    }

    @AfterEach
    void tearDown() {
        truck = null;
    }

    @Test
    void shouldReturnTrueWhenBoxesLoadToTruck() {
        //GIVEN

        Package packageToBeLoaded = createBox("20");
        Package nextPackageToBeLoaded = createBox("80");

        truck.load(packageToBeLoaded);
        truck.load(nextPackageToBeLoaded);

        assertEquals(new BigDecimal("180.00"), truck.getCurrentWeight());
        assertTrue(truck.getPackedPackages().contains(packageToBeLoaded));
        assertEquals(5, truck.getPackedPackages().size());
    }

    @Test
    void shouldLoadBoxWhenWeightIsExactlyEqualToMaxPayload() {
        Package maxWeightPackage = createBox("120");

        truck.load(maxWeightPackage);

        assertEquals(new BigDecimal("200.00"), truck.getCurrentWeight());
        assertTrue(truck.getPackedPackages().contains(maxWeightPackage));
        assertEquals(4, truck.getPackedPackages().size());
    }

    @Test
    void shouldThrowExceptionWhenPayLoadIsExceeded() {
        //GIVEN
        Package heavyWeight = createBox("135");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            truck.load(heavyWeight);
        });

        assertEquals("Truck loading failed", exception.getMessage());
        assertEquals(3, truck.getPackedPackages().size());

        //Werfikacja czy ta paczka napewno nie została dodana

        BigDecimal expectedWeight = new BigDecimal("80.00");
        assertEquals(expectedWeight, truck.getCurrentWeight());
        assertEquals(0, expectedWeight.compareTo(truck.getCurrentWeight()));

        assertFalse(truck.getPackedPackages().isEmpty());
    }

    @Test
    void shouldUnloadBoxSuccessfully() {
        Package packageToUnload = createBox("20");
        truck.load(packageToUnload);

        truck.unload(packageToUnload);
        assertEquals(0, new BigDecimal("80").compareTo(truck.getCurrentWeight()));
    }

    @Test
    void shouldThrowExceptionWhenPayULoadIsExceeded() {
        Package packageToUnload = createBox("40");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                truck.unload(packageToUnload));

        assertEquals("Cannot unload a box that is not on the truck", exception.getMessage());
        assertEquals(3, truck.getPackedPackages().size());

        //Weryfikacja czy paczka napewno została rozładowana
        BigDecimal expectedWeight = new BigDecimal("80.00");
        assertEquals(0, expectedWeight.compareTo(truck.getCurrentWeight()));
    }
}