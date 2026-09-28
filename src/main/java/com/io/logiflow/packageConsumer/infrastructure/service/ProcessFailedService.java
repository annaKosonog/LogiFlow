package com.io.logiflow.packageConsumer.infrastructure.service;

import com.io.logiflow.packageConsumer.domain.dao.ProcessFailed;
import com.io.logiflow.packageConsumer.domain.dto.response.ProcessFailedResponseDto;
import com.io.logiflow.packageConsumer.domain.mapper.ProcessFailedMapper;
import com.io.logiflow.packageConsumer.domain.repository.ProcessFailedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProcessFailedService {

    private final ProcessFailedRepository processFailedRepository;
    private final ProcessFailedMapper processFailedMapper;

    @Transactional(readOnly = true)
    public Page<ProcessFailedResponseDto> getProcessByUuid(UUID processFailedUuid, int page, int size) {
        PageRequest pageable = PageRequest.of(page-1, size);
        Page<ProcessFailed> processFailedByUuid = processFailedRepository.findProcessFailedByUuid(pageable, processFailedUuid);
        return processFailedByUuid.map(processFailedMapper::toDTO);
    }
}
