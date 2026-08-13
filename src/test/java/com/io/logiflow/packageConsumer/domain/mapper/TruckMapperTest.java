package com.io.logiflow.packageConsumer.domain.mapper;

import com.io.logiflow.packageConsumer.domain.dao.Truck;
import com.io.logiflow.packageConsumer.domain.dto.request.CreateTruckDTO;
import com.io.logiflow.packageConsumer.domain.dto.response.TruckDTO;
import com.io.logiflow.packageConsumer.domain.dto.response.TruckResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.UUID;

import static com.io.logiflow.packageConsumer.domain.dao.TruckFixture.createLoadedTruck;
import static com.io.logiflow.packageConsumer.domain.dto.request.CreateTruckDTOFixture.*;
import static com.io.logiflow.packageConsumer.domain.dto.response.PackageFixtureDTO.thirdBoxDTOPackToTruck;
import static com.io.logiflow.packageConsumer.domain.dto.response.TruckDTOFixture.createLoadedTruckDTO;
import static org.junit.jupiter.api.Assertions.*;

public class TruckMapperTest {

    private TruckMapper truckMapper;

    @BeforeEach
    void setUp() {
        CommonMapper commonMapper = new CommonMapperImpl();

        PackageMapperImpl packageMapperImpl = new PackageMapperImpl();
        ReflectionTestUtils.setField(packageMapperImpl, "commonMapper", commonMapper);

        TruckMapperImpl impl = new TruckMapperImpl();
        ReflectionTestUtils.setField(impl, "packageMapper", packageMapperImpl);
        ReflectionTestUtils.setField(impl, "commonMapper", commonMapper);

        truckMapper = impl;
    }

    @Test
    void toDTO_shouldMapAllFieldsCorrectly() {
        Truck truck = createLoadedTruck();

        TruckDTO dto = truckMapper.toDTO(truck);

        assertNotNull(dto);
        assertEquals("528e0004-a6f9-4018-bcba-87e20ad08606", dto.getIdTruck().toString());
        assertEquals("80.00", dto.getCurrentWeight().toString());
        assertEquals(220, dto.getDimensionsDTO().getHeight());
        assertEquals(250, dto.getDimensionsDTO().getWidth());
        assertEquals(600, dto.getDimensionsDTO().getDepth_length());
        assertEquals("200.00", dto.getMaxPayload().toString());
    }

    @Test
    void toDTO_shouldMapPackedPackageList() {
        Truck truck = createLoadedTruck();

        TruckDTO dto = truckMapper.toDTO(truck);

        assertEquals(3, dto.getPackedPackages().size());
        assertEquals(UUID.fromString("3a2b1c4d-5e6f-4a7b-8c9d-0e1f2a3b4c5d"), dto.getPackedPackages().get(0).getId());
    }

    @Test
    void toDTO_shouldReturnNull_whenTruckIsNull() {
        assertNull(truckMapper.toDTO(null));
    }

    @Test
    void toDTO_shouldHandleNullPackedPackagesList() {
        Truck truck = new Truck();
        truck.setUuid(UUID.randomUUID());
        truck.setPackedPackages(null);

        TruckDTO dto = truckMapper.toDTO(truck);

        assertNull(dto.getPackedPackages());
    }

    @Test
    void toEntity_shouldMapAllFieldsCorrectly() {
        TruckDTO dto = createLoadedTruckDTO();

        Truck entity = truckMapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals(UUID.fromString("528e0004-a6f9-4018-bcba-87e20ad08606"), entity.getUuid());
        assertEquals("200", entity.getMaxPayload().toString());
        assertEquals("80", entity.getCurrentWeight().toString());
        assertTrue(entity.getPackedPackages().isEmpty());
    }

    @Test
    void toEntity_shouldReturnNull_whenDtoIsNull() {
        assertNull(truckMapper.toEntity(null));
    }

    @Test
    void toEntity_shouldMapPackedPackagesList() {
        TruckDTO dto = createLoadedTruckDTO();
        dto.setPackedPackages(List.of(thirdBoxDTOPackToTruck()));

        Truck entity = truckMapper.toEntity(dto);

        assertEquals(1, entity.getPackedPackages().size());
        assertEquals(UUID.fromString("f7f4b7d2-660c-4430-bcec-ce3f9dd32c36"), entity.getPackedPackages().get(0).getUuid());
    }

    @Test
    void toEntity_shouldMapCreateTruckDto() {
        CreateTruckDTO createTruckDTO = createFirstTruck();
        Truck entity = truckMapper.toEntityTruck(createTruckDTO);

        assertNotNull(entity);
        assertNotNull(entity.getUuid());
        assertEquals("200.00", entity.getMaxPayload().toString());
        assertEquals(600, entity.getLength());
        assertEquals("WI 1234E", entity.getLicensePlate());
        assertTrue(entity.getPackedPackages().isEmpty());
    }

    @Test
    void toEntity_shouldReturnNull_whenCreateTruckDtoIsNull() {
        assertNull(truckMapper.toEntityTruck(null));
    }

    @Test
    void toEntity_shouldReturnTruck_whenCreateTruckDtoLicensePlateIsNull() {
        CreateTruckDTO createTruckDTO = createNullLicensePlateTruck();
        Truck entity = truckMapper.toEntityTruck(createTruckDTO);
        assertNotNull(entity);
        assertNull(entity.getLicensePlate());
        assertEquals("150", entity.getMaxPayload().toString());
    }

    @Test
    void toEntity_shouldReturnTruck_whenCreateTruckDtoEmptyWeight() {
        CreateTruckDTO emptyWeightTruck = createEmptyWeightTruck();

        Truck entity = truckMapper.toEntityTruck(emptyWeightTruck);
        assertNotNull(entity);
        assertNull(entity.getMaxPayload());
    }

    @Test
    void toTruckResponseDTO_shouldMapTruck() {
        Truck truck = createLoadedTruck();
        TruckResponseDTO truckResponseDTO = truckMapper.toTruckResponseDTO(truck);

        assertNotNull(truckResponseDTO);
        assertEquals(UUID.fromString("528e0004-a6f9-4018-bcba-87e20ad08606"), truckResponseDTO.uuid());
        assertEquals("200.00", truckResponseDTO.maxPayload().toString());
        assertEquals(220, truckResponseDTO.dimensions().getHeight());
        assertEquals("WI 1234E", truckResponseDTO.licensePlate());
    }

    @Test
    void toEntity_shouldReturnNull_whenTruckIsNull() {
        assertNull(truckMapper.toTruckResponseDTO(null));
    }
}
