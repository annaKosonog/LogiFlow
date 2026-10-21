package com.io.logiflow.packageConsumer.domain;

import com.io.logiflow.packageConsumer.infrastructure.dao.Package;

import java.util.List;
import java.util.UUID;

public interface PackageResource {

    // todo change Package JPA to clean java model
    List<Package> findAllByUuidIn(List<UUID> packageId);

}
