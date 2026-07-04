package com.io.logiflow.packageConsumer.domain.repository;

import com.io.logiflow.packageConsumer.domain.dao.Package;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PackageRepository extends JpaRepository<Package, Long> {

    @Query("SELECT p.trackingNumber FROM Package p WHERE p.trackingNumber = :trackingNumber")
    String findByTrackingNumber(String trackingNumber);

    // Zapytanie SQL, które wyciąga kolejny numer z Twojej sekwencji w PostgreSQL
    @Query(value = "SELECT nextval('tracking_number_seq')", nativeQuery = true)
    long getNextTrackingSequenceValue();

    List<Package> findAllByUuidIn(List<UUID> packageId);

    Optional<Package> findPackageByTrackingNumber(String trackingNumber);

    Optional<Package> findPackageByUuid(UUID uuid);
}
