package com.io.logiflow.packageConsumer.infrastructure.repository;

import com.io.logiflow.packageConsumer.domain.PackageResource;
import com.io.logiflow.packageConsumer.infrastructure.dao.Package;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PackageResourceImpl implements PackageResource {
    private final PackageJpaRepository packageJpaRepository;

    @Override
    public List<Package> findAllByUuidIn(List<UUID> packageId) {
        return packageJpaRepository.findAllByUuidIn(packageId);
    }
}
