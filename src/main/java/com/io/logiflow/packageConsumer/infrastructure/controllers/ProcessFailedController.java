package com.io.logiflow.packageConsumer.infrastructure.controllers;

import com.io.logiflow.packageConsumer.domain.dto.response.ProcessFailedResponseDto;
import com.io.logiflow.packageConsumer.exception.web.api.common.ApiResponse;
import com.io.logiflow.packageConsumer.infrastructure.service.ProcessFailedService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/process")
@RequiredArgsConstructor
public class ProcessFailedController {

    private final ProcessFailedService processFailedService;


    @GetMapping
    ResponseEntity<ApiResponse<Page<ProcessFailedResponseDto>>> getProcessFailedWhenUnpacedPackage(
            @RequestParam UUID processFailedUuid,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Page<ProcessFailedResponseDto> processFailedPage = processFailedService.getProcessByUuid(processFailedUuid, page, size);
        return ResponseEntity.ok(ApiResponse.success(processFailedPage));
    }

}
