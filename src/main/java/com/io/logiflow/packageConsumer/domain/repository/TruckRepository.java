package com.io.logiflow.packageConsumer.domain.repository;

import com.io.logiflow.packageConsumer.domain.dao.Truck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TruckRepository extends JpaRepository<Truck, Long> {
    Optional<Truck> findTruckByUuid(UUID uuid);

    List<Truck> findTruckByLength(@Param("length") int length);

    List<Truck> findTruckByMaxPayload(BigDecimal maxPayload);
}
