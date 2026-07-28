package com.io.logiflow.packageConsumer.infrastructure.controllers;

import com.io.logiflow.packageConsumer.domain.dto.PackageCreateRequest;
import com.io.logiflow.packageConsumer.domain.dto.PackageDTO;
import com.io.logiflow.packageConsumer.domain.dto.PackageFilterDTO;
import com.io.logiflow.packageConsumer.exception.ApiResponse;
import com.io.logiflow.packageConsumer.infrastructure.service.PackageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/packages")
@RequiredArgsConstructor
public class PackageController {

    private final PackageService packingService;

    @GetMapping
    ResponseEntity<ApiResponse<Page<PackageDTO>>> getAllPackages(
            @ModelAttribute PackageFilterDTO filter,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Page<PackageDTO> packPage = packingService.getPackages(filter, page, size);
        return ResponseEntity.ok(ApiResponse.success(packPage));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PackageDTO>> registerPackage(@RequestBody PackageCreateRequest createRequest) {
        PackageDTO savedPackage = packingService.registerNewPackage(createRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(savedPackage));
    }

    @PatchMapping("/{packId}")
    public ResponseEntity<ApiResponse<PackageDTO>> updateDimensions(@PathVariable UUID packId, @RequestBody Map<String, Object> fields) {
        PackageDTO updated = packingService.updatePackageFields(packId, fields);
        return ResponseEntity
                .ok(ApiResponse.success(updated));
    }
}
