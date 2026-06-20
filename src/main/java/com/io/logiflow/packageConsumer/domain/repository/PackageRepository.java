package com.io.logiflow.packageConsumer.domain.repository;

import com.io.logiflow.packageConsumer.domain.dao.Package;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PackageRepository extends JpaRepository<Package, Long> {

    @Query("SELECT p.trackingNumber FROM Package p WHERE p.trackingNumber = :trackingNumber")
    String findByTrackingNumber(String trackingNumber);
}
