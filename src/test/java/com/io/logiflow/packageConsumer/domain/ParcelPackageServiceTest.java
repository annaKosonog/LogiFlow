package com.io.logiflow.packageConsumer.domain;

import com.io.logiflow.packageConsumer.infrastructure.ParcelPackingService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.io.logiflow.packageConsumer.domain.PackageFixture.*;
import static com.io.logiflow.packageConsumer.domain.TruckFixture.createBox;
import static com.io.logiflow.packageConsumer.domain.TruckFixture.createLoadedTruck;
import static org.junit.jupiter.api.Assertions.*;


public class ParcelPackageServiceTest {

    private ParcelPackingService parcelPackingService;
    private Truck truck;
    private List<Package> aPackages;

    @BeforeEach
    void setUp() {
        parcelPackingService = new ParcelPackingService();
        truck = createLoadedTruck();
        aPackages = new ArrayList<>();
    }

    @AfterEach
    void tearDown() {
        parcelPackingService = null;
        truck = null;
        aPackages = null;
    }

    @Test
    void testShouldPackSingleBoxCorrectly() {
        // Given
        aPackages.add(createBox("40.00"));
        // When
        parcelPackingService.pack(aPackages, truck);

        // Then
        assertEquals(0, aPackages.get(0).getX());
        assertEquals(0, aPackages.get(0).getY());
        assertEquals(0, aPackages.get(0).getZ());
    }

    @Test
    void shouldPackAllBoxesIntoTruck() {
        aPackages.add(createBox("50.00"));
        aPackages.add(createBox("20.00"));

        parcelPackingService.pack(aPackages, truck);
        assertEquals(2, aPackages.size());
    }


    @Test
    void testShouldNotPackOverweightBox() {
        // Given: Paczka cięższa niż limit (200kg > 201kg)
        aPackages.add(createBox("121.00"));

        // When
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> parcelPackingService.pack(aPackages, truck));
        // Then
        assertEquals("Truck loading failed", exception.getMessage());

    }

    @Test
    void shouldSortBoxesByVolumeAscendingBeforePacking() {
        List<Package> localPackages = new ArrayList<>();
        Package large = large();
        Package small = small();
        Package medium = medium();
        localPackages.add(large);
        localPackages.add(small);
        localPackages.add(medium);

        parcelPackingService.pack(localPackages, truck);

        // po sortedPack lista powinna być: small, medium, large
        assertEquals(small, localPackages.get(0));
        assertEquals(medium, localPackages.get(1));
        assertEquals(large, localPackages.get(2));
    }

    @Test
    void shouldSortBoxesWithSameVolumeByWeightDescending() {
        List<Package> localPackages = new ArrayList<>();
        Package lightPackage = PackageFixture.lightBox();
        Package heavyPackage = PackageFixture.heavyBox();
        localPackages.add(lightPackage);
        localPackages.add(heavyPackage);

        parcelPackingService.pack(localPackages, truck);
        assertEquals(lightPackage, localPackages.get(1));
        assertEquals(heavyPackage, localPackages.get(0));
    }

    @Test
    void shouldPackBoxWhenItFitsExactlyInSpace() {
        aPackages.add(PackageFixture.boxThatFitsExactly(SpaceFixture.largeSpace()));

        parcelPackingService.pack(aPackages, truck);
        assertEquals(3, truck.getPackedPackages().size());
    }

    @Test
    void shouldNotPackBoxWhenTooWide() {
        Package largePackage = largeBox();
        aPackages.add(largePackage);

        parcelPackingService.pack(aPackages, truck);
        assertEquals(2, truck.getPackedPackages().size());
        assertFalse(truck.getPackedPackages().contains(largePackage));
    }

    @Test
    void shouldNotPackBoxWhenTooTall() {
        Package heightPackage = heightBox();
        aPackages.add(PackageFixture.heightBox());

        parcelPackingService.pack(aPackages, truck);
        assertEquals(2, truck.getPackedPackages().size());
        assertFalse(truck.getPackedPackages().contains(heightPackage));
    }

    @Test
    void shouldNotPackBoxWhenTooDeep() {
        Package deepthPackage = deepthBox();
        aPackages.add(deepthPackage);

        parcelPackingService.pack(aPackages, truck);
        assertEquals(2, truck.getPackedPackages().size());
        assertFalse(truck.getPackedPackages().contains(deepthPackage));
    }

}
