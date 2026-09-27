package com.io.logiflow.packageConsumer.domain.mapper;

import com.io.logiflow.packageConsumer.domain.dao.Package;
import com.io.logiflow.packageConsumer.domain.dto.request.PackageCreateRequest;
import com.io.logiflow.packageConsumer.domain.dto.response.DimensionsDTO;
import com.io.logiflow.packageConsumer.domain.dto.response.PackageDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.io.logiflow.packageConsumer.domain.dto.request.PackageCreateRequestTest.exactRequest;
import static org.junit.jupiter.api.Assertions.*;

public class PackageCreateRequestMapperTest {

    private PackageCreateRequestMapper packageCreateRequestMapper;

    @BeforeEach
    void setUp() {
        packageCreateRequestMapper = new PackageCreateRequestMapperImpl();
    }

    @Test
    void toDTO_shouldMapAllFieldsCorrectly() {
        PackageCreateRequest createRequest = exactRequest();

        PackageDTO dto = packageCreateRequestMapper.toDto(createRequest);

        assertNotNull(dto);
        assertEquals(10, dto.getDimensions().getWidth());
        assertEquals(10, dto.getDimensions().getHeight());
        assertEquals(10, dto.getDimensions().getDepth_length());
        assertEquals("5.0", dto.getWeight().toString());
    }

    @Test
    void toDTO_shouldHandleNullPackageCreateRequest() {
        assertNull(packageCreateRequestMapper.toDto(null));
    }

    @Test
    void toDTO_shouldHandleNullCoordinates() {
        PackageCreateRequest createRequest = exactRequest();

        PackageDTO dto = packageCreateRequestMapper.toDto(createRequest);

        assertNotNull(dto);
        assertNull(dto.getCoordinates());
    }

    @Test
    void toEntity_shouldMapAllElements() {
        PackageCreateRequest createRequest = exactRequest();

        Package entity = packageCreateRequestMapper.toPackageDao(createRequest);

        assertNotNull(entity);
        assertNull(entity.getTruck());
        assertEquals(10, entity.getWidth());
        assertEquals(10, entity.getHeight());
        assertEquals(10, entity.getDepth());
    }

    @Test
    void toEntity_shouldReturnNull_whenDtoIsNull() {
        assertNull(packageCreateRequestMapper.toPackageDao(null));
    }

    @Test
    void toEntity_shouldHandleNullTruckID() {
        PackageCreateRequest request = exactRequest();
        request.setDimensions(new DimensionsDTO(10, 20, 30));

        Package entity = packageCreateRequestMapper.toPackageDao(request);

        assertNull(entity.getTruck());
    }


}
