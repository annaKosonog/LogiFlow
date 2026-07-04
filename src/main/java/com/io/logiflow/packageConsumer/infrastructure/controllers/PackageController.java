package com.io.logiflow.packageConsumer.infrastructure.controllers;

import com.io.logiflow.packageConsumer.domain.dto.PackageCreateRequest;
import com.io.logiflow.packageConsumer.domain.dto.PackageDTO;
import com.io.logiflow.packageConsumer.infrastructure.service.PackageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PackageController {

    private final PackageService packingService;

    @GetMapping("/packages")
    ResponseEntity<Page<PackageDTO>> getAllPackages(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<PackageDTO> packPage = packingService.getPackages(page, size);
        return ResponseEntity.ok(packPage);
    }

    @GetMapping("/packages/search")
    ResponseEntity<PackageDTO> getPackagesByFilter(
            @RequestParam(required = false) String trackingNumber,
            @RequestParam(required = false) UUID uuid) {
        PackageDTO filter = packingService.getPackagesByFilter(uuid, trackingNumber);
        return ResponseEntity.ok(filter);
    }

    @PostMapping("/packages")
    public ResponseEntity<PackageDTO> registerPackage(@RequestBody PackageCreateRequest createRequest) {
        PackageDTO savedPackage = packingService.registerNewPackage(createRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPackage);
    }

    @PatchMapping("/packages/{packId}")
    public ResponseEntity<PackageDTO> updateDimensions(@PathVariable Long packId, @RequestBody Map<String, Object> fields) {
        PackageDTO updated = packingService.updatePackageFields(packId, fields);
        return ResponseEntity.ok(updated);
    }
}
