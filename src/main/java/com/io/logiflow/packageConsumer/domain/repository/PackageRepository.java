package com.io.logiflow.packageConsumer.domain.repository;

import com.io.logiflow.packageConsumer.domain.dao.Package;
import com.io.logiflow.packageConsumer.domain.dao.Truck;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PackageRepository extends JpaRepository<Package, Long>, JpaSpecificationExecutor<Package> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p.trackingNumber FROM Package p WHERE p.trackingNumber = :trackingNumber")
    String findByTrackingNumber(String trackingNumber);


    @Query(value = "SELECT MAX(p.package_id) from packages p", nativeQuery = true)
    long getNextTrackingSequenceValue();

    List<Package> findAllByUuidIn(List<UUID> packageId);

    Optional<Package> findPackageByTrackingNumber(String trackingNumber);

    Optional<Package> findPackageByUuid(UUID uuid);

    List<Package> findAllByTruck(Truck truck);

    @EntityGraph(attributePaths = "truck")
    Page<Package> findAll(Specification<Package> spec, Pageable pageable);

}
