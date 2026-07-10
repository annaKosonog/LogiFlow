package com.io.logiflow.packageConsumer.infrastructure.service;

import com.io.logiflow.packageConsumer.domain.dao.Package;
import com.io.logiflow.packageConsumer.domain.dto.PackageDTO;
import com.io.logiflow.packageConsumer.domain.mapper.CommonMapper;
import com.io.logiflow.packageConsumer.domain.mapper.PackageMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

import static com.io.logiflow.packageConsumer.domain.dao.PackageFixture.smallDao;
import static com.io.logiflow.packageConsumer.domain.dto.PackageFixtureDTO.smallDTO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

public class PackageMapperTest {

    private PackageMapper packageMapper;
    private CommonMapper commonMapper;

    @BeforeEach
    void setUp() {
        packageMapper = Mappers.getMapper(PackageMapper.class);
        commonMapper = Mockito.mock(CommonMapper.class);
        ReflectionTestUtils.setField(packageMapper, "commonMapper", commonMapper);
    }


    @Test
    void shouldMapDtoToEntity() {
        PackageDTO dto = smallDTO();
        dto.setTruckID(123L);

        Package entity = packageMapper.toEntity(dto);
        commonMapper.toCoordinatesDTO(entity);

        assertThat(entity.getTruck().getTruckId()).isEqualTo(123L);
        assertThat(entity.getHeight()).isEqualTo(1);
        assertThat(entity.getWeight()).isEqualTo("10.0");
    }

    @Test
    void shouldMapEntityToDto() {
        Package dao = smallDao();

        Mockito.when(commonMapper.toDimensionsDTO(any(Package.class))).thenReturn(smallDTO().getDimensions());
        Mockito.when(commonMapper.toCoordinatesDTO(any(Package.class))).thenReturn(smallDTO().getCoordinates());
        PackageDTO dto = packageMapper.toDTO(dao);

        assertThat(dto.getDimensions().getHeight()).isEqualTo(1);
        assertNotNull(dto);
        assertEquals(1, dto.getDimensions().getWidth());
        assertEquals(1, dto.getDimensions().getHeight());
        assertEquals(1, dto.getDimensions().getWidth());
        assertEquals(new BigDecimal("10"), dto.getWeight());
    }
}
