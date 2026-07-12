package com.io.logiflow.packageConsumer.infrastructure.service;

import com.io.logiflow.packageConsumer.domain.dao.Package;
import com.io.logiflow.packageConsumer.domain.dto.PackageDTO;
import com.io.logiflow.packageConsumer.domain.mapper.CommonMapperImpl;
import com.io.logiflow.packageConsumer.domain.mapper.PackageMapper;
import com.io.logiflow.packageConsumer.domain.mapper.PackageMapperImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.io.logiflow.packageConsumer.domain.dao.PackageFixture.smallDao;
import static com.io.logiflow.packageConsumer.domain.dto.PackageFixtureDTO.mediumDTO;
import static com.io.logiflow.packageConsumer.domain.dto.PackageFixtureDTO.smallDTO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        PackageMapperImpl.class,
        CommonMapperImpl.class
})
public class PackageMapperTest {

    @Autowired
    private PackageMapper packageMapper = Mappers.getMapper(PackageMapper.class);

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
}
