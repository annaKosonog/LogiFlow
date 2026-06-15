package com.io.logiflow.packageConsumer.infrastructure.controllers;

import com.io.logiflow.packageConsumer.domain.Package;
import com.io.logiflow.packageConsumer.infrastructure.PackageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PackageController {

    private final PackageService packingService;

    @GetMapping("/packages")
    ResponseEntity<List<Package>> getAllPackages() {
        List<Package> packages = packingService.getPackages();
        return ResponseEntity.ok(packages);
    }

    @PostMapping("/packages")
    public ResponseEntity<Package> registerPackage(@RequestBody Package pack) {
        Package savedPackage = packingService.registerNewPackage(pack);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPackage);
    }


    @PostMapping("/truck/{truckId}")
    public ResponseEntity<String> packPackages(
            @PathVariable Long truckId,
            @RequestBody List<Package> packages) {
        packingService.addPackagesToTruck(packages, truckId);
        return ResponseEntity.ok("The packing process has been successfully completed for the truck with ID " + truckId);
    }
}
