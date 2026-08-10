package com.io.logiflow.packageConsumer.infrastructure.controllers;

import com.io.logiflow.packageConsumer.domain.dto.PackingRequestDTO;
import com.io.logiflow.packageConsumer.domain.dto.TruckDTO;
import com.io.logiflow.packageConsumer.domain.dto.TruckFilterDto;
import com.io.logiflow.packageConsumer.exception.ApiResponse;
import com.io.logiflow.packageConsumer.infrastructure.service.TruckService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trucks")
@RequiredArgsConstructor
public class TruckController {

    private final TruckService truckService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<TruckDTO>>> getAllTrucks(
            @ModelAttribute TruckFilterDto filter,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Page<TruckDTO> trucks = truckService.getTrucks(filter, page, size);
        return ResponseEntity.ok(ApiResponse.success(trucks));
    }

    //dto idTruck, idPack
    @PostMapping()
    public ResponseEntity<ApiResponse<Void>> packPackages(
            @RequestBody PackingRequestDTO packages) {
        truckService.addPackagesToTruck(packages);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}