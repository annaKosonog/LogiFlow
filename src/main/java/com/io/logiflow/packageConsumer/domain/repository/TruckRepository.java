package com.io.logiflow.packageConsumer.domain.repository;

import com.io.logiflow.packageConsumer.domain.Truck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TruckRepository extends JpaRepository<Truck, Long> {
}
