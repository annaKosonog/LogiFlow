package com.io.logiflow.packageConsumer.infrastructure.service;

import com.io.logiflow.packageConsumer.domain.dao.Package;
import com.io.logiflow.packageConsumer.domain.dto.PackageDTO;
import com.io.logiflow.packageConsumer.domain.mapper.PackageMapper;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static com.io.logiflow.packageConsumer.domain.dao.PackageFixture.smallDao;
import static com.io.logiflow.packageConsumer.domain.dto.PackageFixtureDTO.smallDTO;
import static org.assertj.core.api.Assertions.assertThat;

public class PackageMapperTest {

    private final PackageMapper packageMapper = Mappers.getMapper(PackageMapper.class);

    @Test
    void shouldMapDtoToEntity() {
        PackageDTO dto = smallDTO();
        dto.setTruckID(123L);

        Package entity = packageMapper.toEntity(dto);

        assertThat(entity.getTruck().getTruckId()).isEqualTo(123L);
    }

    @Test
    void shouldMapEntityToDto() {
        Package dao = smallDao();
        dao.setHeight(50);

        PackageDTO dto = packageMapper.toDTO(dao);

        assertThat(dto.getDimensions().getHeight()).isEqualTo(50);


    }
}
