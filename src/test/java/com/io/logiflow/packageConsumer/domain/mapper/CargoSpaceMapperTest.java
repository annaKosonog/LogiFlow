package com.io.logiflow.packageConsumer.domain.mapper;

import com.io.logiflow.packageConsumer.domain.dao.CargoSpace;
import com.io.logiflow.packageConsumer.domain.dto.response.CargoSpaceDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static com.io.logiflow.packageConsumer.domain.dao.CargoSpaceFixture.smallSpace;
import static com.io.logiflow.packageConsumer.domain.dto.response.CargoSpaceDTOFixture.boxFits;
import static org.junit.jupiter.api.Assertions.*;

public class CargoSpaceMapperTest {

    private CargoSpaceMapper cargoSpaceMapper;

    @BeforeEach
    void setUp() {
        CommonMapper commonMapper = new CommonMapperImpl();

        PackageMapperImpl packageMapperImpl = new PackageMapperImpl();
        ReflectionTestUtils.setField(packageMapperImpl, "commonMapper", commonMapper);

        TruckMapperImpl truckMapperImpl = new TruckMapperImpl();
        ReflectionTestUtils.setField(truckMapperImpl, "packageMapper", packageMapperImpl);
        ReflectionTestUtils.setField(truckMapperImpl, "commonMapper", commonMapper);

        CargoSpaceMapperImpl cargoSpaceMapperImpl = new CargoSpaceMapperImpl();
        ReflectionTestUtils.setField(cargoSpaceMapperImpl, "truckMapper", truckMapperImpl);

        cargoSpaceMapper = cargoSpaceMapperImpl;
    }

    @Test
    void toDTO_shouldMapAllFieldsCorrectly() {
        CargoSpace cargoSpace = smallSpace();

        CargoSpaceDTO dto = cargoSpaceMapper.toDTO(cargoSpace);

        assertNotNull(dto);
        assertEquals(0, dto.getCoordinatesDTO().getX());
        assertEquals(0, dto.getCoordinatesDTO().getY());
        assertEquals(0, dto.getCoordinatesDTO().getZ());
        assertEquals(2, dto.getDimensionsDTO().getHeight());
        assertEquals(2, dto.getDimensionsDTO().getDepth_length());
    }

    @Test
    void toDTO_shouldReturnNull_whenSpaceIsNull() {
        assertNull(cargoSpaceMapper.toDTO(null));
    }

    @Test
    void toEntity_shouldMapAllFieldsCorrectly() {
        CargoSpaceDTO dto = boxFits();

        CargoSpace entity = cargoSpaceMapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals(UUID.fromString("a5c8f3e1-9d2b-4f6a-8c1e-3b7d5e9a2f4c").toString(), entity.getUuid().toString());
    }

    @Test
    void toEntity_shouldReturnNull_whenDtoIsNull() {
        assertNull(cargoSpaceMapper.toEntity(null));
    }




}
