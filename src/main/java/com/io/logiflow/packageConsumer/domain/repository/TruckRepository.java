package com.io.logiflow.packageConsumer.domain.repository;

import com.io.logiflow.packageConsumer.domain.dao.Truck;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TruckRepository extends JpaRepository<Truck, Long>, JpaSpecificationExecutor<Truck> {

    @Query("SELECT t FROM Truck t LEFT JOIN FETCH t.packedPackages WHERE t.uuid = :uuid")
    Optional<Truck> findTruckByUuid(UUID uuid);


    Page<Truck> findAll(Specification<Truck> spec, Pageable pageable);

    boolean existsByLicensePlate(String licensePlat);
}
