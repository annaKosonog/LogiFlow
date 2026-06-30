package com.io.logiflow.packageConsumer.infrastructure.controllers;

import com.io.logiflow.packageConsumer.domain.dto.PackageDTO;
import com.io.logiflow.packageConsumer.domain.dto.TruckDTO;
import com.io.logiflow.packageConsumer.infrastructure.service.TruckService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TruckController {

    private final TruckService truckService;

    @GetMapping("/trucks")
    public ResponseEntity<Page<TruckDTO>> getAllTrucks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<TruckDTO> trucks = truckService.getTrucks(page, size);
        return ResponseEntity.ok(trucks);
    }

    @PostMapping("/truck/{truckId}")
    public ResponseEntity<String> packPackages(
            @PathVariable Long truckId,
            @RequestBody List<PackageDTO> packages) {
        truckService.addPackagesToTruck(packages, truckId);
        return ResponseEntity.ok("The packing process has been successfully completed for the truck with ID " + truckId);
    }
}