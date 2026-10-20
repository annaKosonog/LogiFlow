package com.io.logiflow.packageConsumer.infrastructure.mapper;

import com.io.logiflow.packageConsumer.infrastructure.dao.Package;
import com.io.logiflow.packageConsumer.infrastructure.dto.request.PackageCreateRequest;
import com.io.logiflow.packageConsumer.infrastructure.dto.response.PackageDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = CommonMapper.class)
public interface PackageCreateRequestMapper {


    @Mapping(source = "dimensions.width", target = "width")
    @Mapping(source = "dimensions.height", target = "height")
    @Mapping(source = "dimensions.depth_length", target = "depth")
    Package toPackageDao(PackageCreateRequest pack);

    PackageDTO toDto(PackageCreateRequest createRequest);

}
