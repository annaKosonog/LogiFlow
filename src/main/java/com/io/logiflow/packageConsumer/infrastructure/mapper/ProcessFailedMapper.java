package com.io.logiflow.packageConsumer.infrastructure.mapper;

import com.io.logiflow.packageConsumer.infrastructure.dao.ProcessFailed;
import com.io.logiflow.packageConsumer.infrastructure.dto.request.ProcessFailedRequestDto;
import com.io.logiflow.packageConsumer.infrastructure.dto.response.ProcessFailedResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProcessFailedMapper {



    ProcessFailed toEntity(ProcessFailedResponseDto processFailedResponseDto);

    ProcessFailedResponseDto toDTO(ProcessFailed processFailed);


    @Mapping(target = "uuid", ignore = true)
    ProcessFailed toEntityRequest(ProcessFailedRequestDto processFailedRequestDto);
}
