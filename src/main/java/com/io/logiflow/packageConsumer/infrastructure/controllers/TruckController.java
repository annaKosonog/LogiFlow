package com.io.logiflow.packageConsumer.infrastructure.controllers;

import com.io.logiflow.packageConsumer.domain.dto.PackageDTO;
import com.io.logiflow.packageConsumer.domain.dto.TruckDTO;
import com.io.logiflow.packageConsumer.exception.ApiResponse;
import com.io.logiflow.packageConsumer.infrastructure.service.TruckService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/trucks")
@RequiredArgsConstructor
public class TruckController {

    private final TruckService truckService;

    @GetMapping
    public ResponseEntity<Page<TruckDTO>> getAllTrucks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<TruckDTO> trucks = truckService.getTrucks(page, size);
        return ResponseEntity.ok(trucks);
    }

    //todo dodać paginacja endoint po wszystkich co się da + paginacja + połączyc z getAllTrucks
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<TruckDTO>>> getTrucksByFilterLengthAndWeight(
            @RequestParam(required = false, defaultValue = "-1") int length,
            @RequestParam(required = false) BigDecimal weight) {
        List<TruckDTO> filter = truckService.getTrucksByLengthAndWeight(length, weight);
        return ResponseEntity.ok(ApiResponse.success(filter));
    }

    @PostMapping("/{idTruck}")
    public ResponseEntity<ApiResponse<Void>> packPackages(
            @PathVariable UUID idTruck,
            @RequestBody List<PackageDTO> packages) {
        truckService.addPackagesToTruck(packages, idTruck);
        return ResponseEntity.ok(ApiResponse.success(null, "The packing process has been successfully completed for the truck with ID " + idTruck));
    }
}