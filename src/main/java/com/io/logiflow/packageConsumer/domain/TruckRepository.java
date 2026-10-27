package com.io.logiflow.packageConsumer.domain;

import com.io.logiflow.packageConsumer.infrastructure.dao.Truck;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;
import java.util.UUID;

public interface TruckRepository {

    Optional<TruckModel> findTruckByUuid(UUID uuid);

    Page<Truck> findAll(Specification<Truck> spec, Pageable pageable);

    boolean existsByLicensePlate(String licensePlat);

    TruckModel save(TruckModel truck);
}
