package com.io.logiflow.packageConsumer.infrastructure.controllers;

import com.io.logiflow.packageConsumer.domain.dao.Package;
import com.io.logiflow.packageConsumer.domain.dto.PackageDTO;
import com.io.logiflow.packageConsumer.infrastructure.service.PackageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

   /* @PostMapping("/packages")
    public ResponseEntity<Package> registerPackage(@RequestBody Package pack) {
        Package savedPackage = packingService.registerNewPackage(pack);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPackage);
    }*/

    @PatchMapping("/packages/{packId}")
    public ResponseEntity<Package> updateDimensions(@PathVariable Long packId, @RequestBody Map<String, Object> fields) {
        Package updated = packingService.updatePackageFields(packId, fields);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/truck/{truckId}")
    public ResponseEntity<String> packPackages(
            @PathVariable Long truckId,
            @RequestBody List<Package> packages) {
        packingService.addPackagesToTruck(packages, truckId);
        return ResponseEntity.ok("The packing process has been successfully completed for the truck with ID " + truckId);
    }
}
