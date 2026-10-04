package com.io.logiflow.packageConsumer.infrastructure.service.parcel_preparation;

import com.io.logiflow.packageConsumer.domain.dao.Package;
import com.io.logiflow.packageConsumer.domain.dto.response.CoordinatesDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.io.logiflow.packageConsumer.domain.dao.PackageFixture.aPackage;
import static com.io.logiflow.packageConsumer.domain.dao.PackageFixture.mediumDao;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ParcelPlacementFinderTest {

    @Mock
    CargoGrid grid;

    @InjectMocks
    ParcelPlacementFinder parcelPlacementFinder;

    @Test
    void shouldReturnOriginEmptyTruckNotRotated() {
        Package aPackage = mediumDao();
        when(grid.isFree(anyInt(), anyInt(), anyInt(), anyInt(), anyInt(), anyInt()))
                .thenReturn(true);

        Optional<CoordinatesDTO> result = parcelPlacementFinder.findFreePosition(grid, aPackage, 10, 10, 10);
        assertEquals(Optional.of(new CoordinatesDTO(0, 0, 0)), result);
        assertFalse(aPackage.isRotated());
    }

    @Test
    void shouldReturnScansXFirstThenYAndEndZ() {
        when(grid.isFree(anyInt(), anyInt(), anyInt(), anyInt(), anyInt(), anyInt()))
                .thenAnswer(inv -> {
                    int y = inv.getArgument(1);
                    int z = inv.getArgument(2);
                    return z >= 1 && y >= 1;
                });

        var result = parcelPlacementFinder.findFreePosition(grid, aPackage(1, 1, 1), 5, 5, 5);
        assertEquals(Optional.of(new CoordinatesDTO(0, 1, 1)), result);
    }

    @Test
    void shouldReturnPackageThatEqualToTruckNotRotated() {
        when(grid.isFree(0, 0, 0, 4, 3, 5)).thenReturn(true);
        var result = parcelPlacementFinder.findFreePosition(grid, aPackage(4, 3, 5), 4, 3, 5);
        assertEquals(Optional.of(new CoordinatesDTO(0, 0, 0)), result);
    }

    @Test
    void shouldReturnTrueWhenSetRotatedIsTrueAndFitsOnly() {
        when(grid.isFree(0, 0, 0, 2, 1, 4)).thenReturn(true);
        Package p = aPackage(4, 1, 2);
        var result = parcelPlacementFinder.findFreePosition(grid, p, 2, 1, 4);
        assertEquals(Optional.of(new CoordinatesDTO(0, 0, 0)), result);

        assertTrue(p.isRotated());
    }

    @Test
    void shouldReturnCoordinatesAndPrefersNotRotatedWhenBothFit() {
        Package p = aPackage(2, 1, 3);
        when(grid.isFree(anyInt(), anyInt(), anyInt(), anyInt(), anyInt(), anyInt()))
                .thenReturn(true);

        parcelPlacementFinder.findFreePosition(grid, p, 10, 10, 10);
        assertFalse(p.isRotated());
    }

    @Test
    void shouldRotatedScanAndSwapsWhenWidthAndDepthPassedToGrid() {
        Package p = aPackage(4, 1, 2);

        when(grid.isFree(anyInt(), anyInt(), anyInt(), eq(4), eq(1), eq(2)))
                .thenReturn(false);

        when(grid.isFree(anyInt(), anyInt(), anyInt(), eq(2), eq(1), eq(4)))
                .thenReturn(true);

        parcelPlacementFinder.findFreePosition(grid, p, 10, 10, 10);
        verify(grid).isFree(0, 0, 0, 2, 1, 4);   // w=depth, d=width
    }

    @Test
    void shouldReturnEmptyWhenNoSpaceAndDoesNotMutateRotated() {
        when(grid.isFree(anyInt(), anyInt(), anyInt(), anyInt(), anyInt(), anyInt()))
                .thenReturn(false);
        Package p = aPackage(2, 2, 2);
        p.setRotated(true);   // wartość początkowa, której nie wolno zmienić

        var result = parcelPlacementFinder.findFreePosition(grid, p, 5, 5, 5);
        assertTrue(result.isEmpty());
        assertTrue(p.isRotated());
    }

    @Test
    void packageTooTall_returnsEmpty_withoutAskingGrid() {
        var result = parcelPlacementFinder.findFreePosition(grid, aPackage(1, 6, 1), 5, 5, 5);
        assertTrue(result.isEmpty());
        verifyNoInteractions(grid);
    }


}