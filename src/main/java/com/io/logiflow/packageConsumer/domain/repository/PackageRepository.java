package com.io.logiflow.packageConsumer.domain.repository;

import com.io.logiflow.packageConsumer.domain.Package;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PackageRepository extends JpaRepository<Package, Long> {

    String findByTrackingNumber(String trackingNumber);
}
