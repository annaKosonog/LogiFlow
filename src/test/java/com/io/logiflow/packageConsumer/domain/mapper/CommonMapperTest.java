package com.io.logiflow.packageConsumer.domain.mapper;

import com.io.logiflow.packageConsumer.domain.dao.Package;
import com.io.logiflow.packageConsumer.domain.dao.Truck;
import com.io.logiflow.packageConsumer.domain.dto.response.CoordinatesDTO;
import com.io.logiflow.packageConsumer.domain.dto.response.DimensionsDTO;
import org.junit.jupiter.api.Test;

import static com.io.logiflow.packageConsumer.domain.dao.PackageFixture.smallDao;
import static com.io.logiflow.packageConsumer.domain.dao.TruckFixture.createSecondTruckWithPayload;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class CommonMapperTest {

    private final CommonMapper commonMapper = new CommonMapperImpl();

    @Test
    void toDimensionsDTO_shouldMapPackageFieldsCorrectly() {
        Package pack = smallDao();


        DimensionsDTO dto = commonMapper.toDimensionsDTO(pack);

        assertEquals(1, dto.getWidth());
        assertEquals(1, dto.getHeight());
        assertEquals(1, dto.getDepth_length());
    }

    @Test
    void toDimensionsDTO_shouldReturnNull_whenPackageIsNull() {
        assertNull(commonMapper.toDimensionsDTO((Package) null));
    }

    @Test
    void toDimensionsDTO_shouldMapTruckFieldsCorrectly() {
        Truck truck = createSecondTruckWithPayload("150.00");

        DimensionsDTO dto = commonMapper.toDimensionsDTO(truck);

        assertEquals(178, dto.getWidth());
        assertEquals(190, dto.getHeight());
        assertEquals(430, dto.getDepth_length());
    }

    @Test
    void toDimensionsDTO_shouldReturnNull_whenTruckIsNull() {
        assertNull(commonMapper.toDimensionsDTO((Truck) null));
    }

    @Test
    void toCoordinatesDTO_shouldMapFieldsCorrectly() {
        Package pack = new Package();
        pack.setCoordX(1);
        pack.setCoordY(2);
        pack.setCoordZ(3);

        CoordinatesDTO dto = commonMapper.toCoordinatesDTO(pack);

        assertEquals(1, dto.x());
        assertEquals(2, dto.y());
        assertEquals(3, dto.z());
    }

    @Test
    void toCoordinatesDTO_shouldReturnNull_whenPackageIsNull() {
        assertNull(commonMapper.toCoordinatesDTO(null));
    }


}
