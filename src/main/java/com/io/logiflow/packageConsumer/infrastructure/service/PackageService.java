package com.io.logiflow.packageConsumer.infrastructure.service;

import com.io.logiflow.packageConsumer.domain.dao.Package;
import com.io.logiflow.packageConsumer.domain.dto.request.PackageCreateRequest;
import com.io.logiflow.packageConsumer.domain.dto.request.PackageFilterDTO;
import com.io.logiflow.packageConsumer.domain.dto.request.UpdatePackageDimensionsRequestDTO;
import com.io.logiflow.packageConsumer.domain.dto.response.PackageDTO;
import com.io.logiflow.packageConsumer.domain.mapper.PackageCreateRequestMapper;
import com.io.logiflow.packageConsumer.domain.mapper.PackageMapper;
import com.io.logiflow.packageConsumer.domain.repository.PackageRepository;
import com.io.logiflow.packageConsumer.domain.validator.DimensionValidator;
import com.io.logiflow.packageConsumer.domain.validator.PackageValidator;
import com.io.logiflow.packageConsumer.exception.ResourceNotFoundException;
import com.io.logiflow.packageConsumer.infrastructure.specification.PackageSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.io.logiflow.packageConsumer.domain.validator.DimensionValidator.validateDimensions;

@Service
@RequiredArgsConstructor
public class PackageService {

    private final PackageRepository packageRepository;
    private final PackageMapper packageMapper;
    private final PackageCreateRequestMapper createRequestMapper;


    @Transactional
    public PackageDTO registerNewPackage(PackageCreateRequest pack) {
        validateNewPackageRequest(pack);
        long nextSeq = packageRepository.getNextTrackingSequenceValue();
        Package entity = createRequestMapper.toPackageDao(pack);
        String generatedTrackingNumber = entity.generateTrackingNumberAutomatically(nextSeq);
        String checkTrackingNumber = packageRepository.findByTrackingNumber(generatedTrackingNumber);
        if (checkTrackingNumber != null) {
            throw new RuntimeException("The package with the number " + generatedTrackingNumber + " already exist");
        }

        entity.setUuid(UUID.randomUUID());
        entity.setTrackingNumber(generatedTrackingNumber);
        entity.setCoordX(0);
        entity.setCoordY(0);
        entity.setCoordZ(0);

        Package savedEntity = packageRepository.save(entity);
        return packageMapper.toDTO(savedEntity);
    }

    @Transactional(readOnly = true)
    public Page<PackageDTO> getPackages(PackageFilterDTO filter, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Specification<Package> specification = PackageSpecifications.buildSpecification(filter);
        Page<Package> packPage = packageRepository.findAll(specification, pageable);
        return packPage.map(packageMapper::toDTO);
    }

    @Transactional
    public PackageDTO updatePackageFields(UUID packId, UpdatePackageDimensionsRequestDTO fields) {
        if (fields == null || !hasAnyField(fields)) {
            throw new IllegalArgumentException("At least one field must be provided");
        }

        validatePackageDimensions(fields);

        Package existingPackage = packageRepository.findPackageByUuid(packId)
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
        return packageRepository.findPackageByUuid(uuid)
                .map(packageMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Package with UUID number " + uuid +
                        " not found"));
    }

    @Transactional(readOnly = true)
    public PackageDTO getPackagesByTrackingNumber(String trackingNumber) {
        PackageValidator.validateTrackingNumber(trackingNumber);
        return packageRepository.findPackageByTrackingNumber(trackingNumber)
                .map(packageMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Package with tracking number " + trackingNumber +
                        " not found"));
    }

    private void validatePackageDimensions(UpdatePackageDimensionsRequestDTO request) {
        if (request == null) return;
        DimensionValidator.validatePositiveDimensions(
                new DimensionValidator.NamedDimension("Weight", request.weight()),
                new DimensionValidator.NamedDimension("Length", request.length()),
                new DimensionValidator.NamedDimension("Width", request.width()),
                new DimensionValidator.NamedDimension("Height", request.height()));
    }

    private void validateNewPackageRequest(PackageCreateRequest pack) {
        if (pack == null || pack.getDimensions() == null || pack.getWeight() == null) {
            throw new IllegalArgumentException("Dimensions and weight are required");
        }

        // int z getDepth_length(), getWidth(), getHeight() is automatically converted (autoboxed) to an Integer!
        validateDimensions(pack.getDimensions(), "Package dimension");
    }

    private boolean hasAnyField(UpdatePackageDimensionsRequestDTO request) {
        return request.weight() != null || request.length() != null
                || request.width() != null || request.height() != null;
    }
}