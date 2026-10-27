package com.io.logiflow.packageConsumer.infrastructure.repository;

import com.io.logiflow.packageConsumer.domain.TruckModel;
import com.io.logiflow.packageConsumer.domain.TruckRepository;
import com.io.logiflow.packageConsumer.infrastructure.dao.Truck;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TruckRepositoryImpl implements TruckRepository {
    private final TruckJpaRepository truckJpaRepository;
    // todo mapper Truck (entity) -> TruckModel (raw java)

    @Override
    public Optional<TruckModel> findTruckByUuid(UUID uuid) {
        return truckJpaRepository.findTruckByUuid(uuid);
    }

    @Override
    public Page<Truck> findAll(Specification<Truck> spec, Pageable pageable) {
        return truckJpaRepository.findAll(spec, pageable);
    }

    @Override
    public boolean existsByLicensePlate(String licensePlat) {
        return truckJpaRepository.existsByLicensePlate(licensePlat);
    }

    @Override
    public TruckModel save(TruckModel truck) {
        return truckJpaRepository.save(truck);
    }
}
