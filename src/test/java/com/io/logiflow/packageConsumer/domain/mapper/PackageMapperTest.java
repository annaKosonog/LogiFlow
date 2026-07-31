package com.io.logiflow.packageConsumer.domain.mapper;

import com.io.logiflow.packageConsumer.domain.dao.Package;
import com.io.logiflow.packageConsumer.domain.dao.Truck;
import com.io.logiflow.packageConsumer.domain.dto.DimensionsDTO;
import com.io.logiflow.packageConsumer.domain.dto.PackageDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.io.logiflow.packageConsumer.domain.dao.PackageFixture.smallDao;
import static com.io.logiflow.packageConsumer.domain.dto.PackageFixtureDTO.mediumDTO;
import static com.io.logiflow.packageConsumer.domain.dto.PackageFixtureDTO.smallDTO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class PackageMapperTest {

    private PackageMapper packageMapper;

    @BeforeEach
    void setUp() {
        PackageMapperImpl impl = new PackageMapperImpl();
        ReflectionTestUtils.setField(impl, "commonMapper", new CommonMapperImpl());
        packageMapper = impl;
    }

    @Test
    void shouldMapDtoToEntity() {
        PackageDTO dto = smallDTO();
        dto.setTruckID(123L);

        Package entity = packageMapper.toEntity(dto);

        assertThat(entity.getTruck().getTruckId()).isEqualTo(123L);
        assertThat(entity.getHeight()).isEqualTo(1);
        assertThat(entity.getWeight()).isEqualTo("10.0");
    }

    @Test
    void shouldMapEntityToDto() {
        Package dao = smallDao();

        PackageDTO dto = packageMapper.toDTO(dao);

        assertThat(dto.getDimensions().getHeight()).isEqualTo(1);
        assertNotNull(dto);
        assertEquals(1, dto.getDimensions().getWidth());
        assertEquals(1, dto.getDimensions().getHeight());
        assertEquals(1, dto.getDimensions().getWidth());
        assertEquals(new BigDecimal("10"), dto.getWeight());
    }

    @Test
    void shouldMapDtoListToEntityList() {
        List<PackageDTO> packageDTOList = new ArrayList<>();
        packageDTOList.add(smallDTO());
        packageDTOList.add(mediumDTO());

        List<Package> result = packageMapper.toEntityList(packageDTOList);

        assertThat(result)
                .isNotNull()
                .hasSize(2);

        assertThat(result.get(0).getUuid().toString()).isNotBlank();
        assertThat(result.get(0).getTrackingNumber()).isEqualTo(smallDTO().getTrackingNumber());

        assertThat(result.get(1).getUuid().toString()).isNotBlank();
        assertThat(result.get(1).getTrackingNumber()).isEqualTo(mediumDTO().getTrackingNumber());
    }

    @Test
    void toDTO_shouldHandleNullTruck() {
        Package pack = new Package();
        pack.setUuid(UUID.randomUUID());
        pack.setTruck(null);
        pack.setWidth(10);
        pack.setHeight(20);
        pack.setDepth(30);

        PackageDTO dto = packageMapper.toDTO(pack);

        assertNotNull(dto);
        assertNull(dto.getTruckID());
    }

    @Test
    void toEntity_shouldReturnNull_whenDtoIsNull() {
        assertNull(packageMapper.toEntity(null));
    }

    @Test
    void toEntity_shouldHandleNullTruckID() {
        PackageDTO dto = new PackageDTO();
        dto.setId(UUID.randomUUID());
        dto.setTruckID(null);
        dto.setDimensions(new DimensionsDTO(10, 20, 30));

        Package entity = packageMapper.toEntity(dto);

        assertNull(entity.getTruck());
    }

    @Test
    void mapIdToTruck_shouldCreateTruckWithGivenId() {
        Truck truck = packageMapper.mapIdToTruck(9L);
        assertNotNull(truck);
        assertEquals(9L, truck.getTruckId());
    }

}
