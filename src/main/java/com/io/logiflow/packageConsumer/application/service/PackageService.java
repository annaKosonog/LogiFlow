package com.io.logiflow.packageConsumer.application.service;

import com.io.logiflow.packageConsumer.infrastructure.dao.Package;
import com.io.logiflow.packageConsumer.infrastructure.dto.request.PackageCreateRequest;
import com.io.logiflow.packageConsumer.infrastructure.dto.request.PackageFilterDTO;
import com.io.logiflow.packageConsumer.infrastructure.dto.request.UpdatePackageDimensionsRequestDTO;
import com.io.logiflow.packageConsumer.infrastructure.dto.response.DimensionsDTO;
import com.io.logiflow.packageConsumer.infrastructure.dto.response.PackageDTO;
import com.io.logiflow.packageConsumer.infrastructure.mapper.PackageCreateRequestMapper;
import com.io.logiflow.packageConsumer.infrastructure.mapper.PackageMapper;
import com.io.logiflow.packageConsumer.infrastructure.repository.PackageJpaRepository;
import com.io.logiflow.packageConsumer.domain.validator.DimensionValidator;
import com.io.logiflow.packageConsumer.domain.validator.PackageValidator;
import com.io.logiflow.packageConsumer.infrastructure.exception.ResourceNotFoundException;
import com.io.logiflow.packageConsumer.infrastructure.specification.PackageSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PackageService {

    private final PackageJpaRepository packageJpaRepository;
    private final PackageMapper packageMapper;
    private final PackageCreateRequestMapper createRequestMapper;


    @Transactional
    public PackageDTO registerNewPackage(PackageCreateRequest pack) {
        validateNewPackageRequest(pack);

        long nextSeq = packageJpaRepository.getNextTrackingSequenceValue()+1;
        Package entity = createRequestMapper.toPackageDao(pack);
        String generatedTrackingNumber = entity.generateTrackingNumberAutomatically(nextSeq);
        String checkTrackingNumber = packageJpaRepository.findByTrackingNumber(generatedTrackingNumber);
        if (checkTrackingNumber != null) {
            throw new RuntimeException("The package with the number " + generatedTrackingNumber + " already exist");
        }

        entity.setUuid(UUID.randomUUID());
        entity.setTrackingNumber(generatedTrackingNumber);
        entity.setCoordX(0);
        entity.setCoordY(0);
        entity.setCoordZ(0);

        Package savedEntity = packageJpaRepository.save(entity);
        return packageMapper.toDTO(savedEntity);
    }

    @Transactional(readOnly = true)
    public Page<PackageDTO> getPackages(PackageFilterDTO filter, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Specification<Package> specification = PackageSpecifications.buildSpecification(filter);
        Page<Package> packPage = packageJpaRepository.findAll(specification, pageable);
        return packPage.map(packageMapper::toDTO);
    }

    @Transactional
    public PackageDTO updatePackageFields(UUID packId, UpdatePackageDimensionsRequestDTO fields) {
        if (fields == null || !hasAnyField(fields)) {
            throw new IllegalArgumentException("At least one field must be provided");
        }

        validatePackageDimensions(fields);

        Package existingPackage = packageJpaRepository.findPackageByUuid(packId)
                .orElseThrow(() -> new ResourceNotFoundException("Package not found"));

        if (fields.weight() != null) existingPackage.setWeight(fields.weight());
        if (fields.length() != null) existingPackage.setDepth(fields.length());
        if (fields.width() != null) existingPackage.setWidth(fields.width());
        if (fields.height() != null) existingPackage.setHeight(fields.height());

        return packageMapper.toDTO(existingPackage);
    }

    @Transactional(readOnly = true)
    public PackageDTO getPackagesByUUID(UUID uuid) {
        PackageValidator.validateFilters(uuid);
        return packageJpaRepository.findPackageByUuid(uuid)
                .map(packageMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Package with UUID number " + uuid +
                        " not found"));
    }

    @Transactional(readOnly = true)
    public PackageDTO getPackagesByTrackingNumber(String trackingNumber) {
        PackageValidator.validateTrackingNumber(trackingNumber);
        return packageJpaRepository.findPackageByTrackingNumber(trackingNumber)
                .map(packageMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Package with tracking number " + trackingNumber +
                        " not found"));
    }

    private void validatePackageDimensions(UpdatePackageDimensionsRequestDTO request) {
        if (request == null) return;
        DimensionValidator.validatePackage(
                request.weight(), request.length(), request.width(), request.height());
    }

    private void validateNewPackageRequest(PackageCreateRequest pack) {
        if (pack == null || pack.getDimensions() == null || pack.getWeight() == null) {
            throw new IllegalArgumentException("Dimensions and weight are required");
        }
        DimensionsDTO d = pack.getDimensions();
        DimensionValidator.validatePackage(
                pack.getWeight(), d.getDepth_length(), d.getWidth(), d.getHeight());
    }

    private boolean hasAnyField(UpdatePackageDimensionsRequestDTO request) {
        return request.weight() != null || request.length() != null
                || request.width() != null || request.height() != null;
    }
}