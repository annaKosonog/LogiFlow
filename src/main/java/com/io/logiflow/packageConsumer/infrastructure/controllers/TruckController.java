package com.io.logiflow.packageConsumer.infrastructure.controllers;

import com.io.logiflow.packageConsumer.domain.Truck;
import com.io.logiflow.packageConsumer.domain.repository.TruckRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TruckController {

    @Autowired
    private TruckRepository truckRepository;


    @GetMapping("/trucks")
    public List<Truck> getAllTrucks() {
        return truckRepository.findAll();
    }
}
