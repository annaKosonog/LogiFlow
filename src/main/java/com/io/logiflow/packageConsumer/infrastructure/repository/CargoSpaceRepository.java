package com.io.logiflow.packageConsumer.infrastructure.repository;

import com.io.logiflow.packageConsumer.infrastructure.dao.CargoSpace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CargoSpaceRepository extends JpaRepository<CargoSpace, Long> {
}
