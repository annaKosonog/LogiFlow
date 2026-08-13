package com.io.logiflow.packageConsumer.infrastructure.service;

import com.io.logiflow.packageConsumer.domain.dao.Package;
import com.io.logiflow.packageConsumer.domain.dto.request.PackageCreateRequest;
import com.io.logiflow.packageConsumer.domain.dto.request.PackageFilterDTO;
import com.io.logiflow.packageConsumer.domain.dto.request.UpdatePackageDimensionsRequestDTO;
import com.io.logiflow.packageConsumer.domain.dto.response.DimensionsDTO;
import com.io.logiflow.packageConsumer.domain.dto.response.PackageDTO;
import com.io.logiflow.packageConsumer.domain.mapper.PackageCreateRequestMapper;
import com.io.logiflow.packageConsumer.domain.mapper.PackageMapper;
import com.io.logiflow.packageConsumer.domain.repository.PackageRepository;
import com.io.logiflow.packageConsumer.domain.validator.DimensionValidator;
import com.io.logiflow.packageConsumer.exception.ResourceNotFoundException;
import com.io.logiflow.packageConsumer.infrastructure.specification.PackageSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static com.io.logiflow.packageConsumer.domain.validator.DimensionValidator.validatePositiveDimensions;

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

    public Page<PackageDTO> getPackages(PackageFilterDTO filter, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Specification<Package> specification = PackageSpecifications.buildSpecification(filter);
        Page<Package> packPage = packageRepository.findAll(specification, pageable);
        return packPage.map(packageMapper::toDTO);
    }

    @Transactional
    public PackageDTO updatePackageFields(UUID packId, UpdatePackageDimensionsRequestDTO fields) { //waga, wymiary, adres dostawy
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

    public PackageDTO getPackagesByUUID(UUID uuid) {
        validateFilters(uuid);
        return packageRepository.findPackageByUuid(uuid)
                .map(packageMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Package with UUID number " + uuid +
                        " not found"));
    }

    public PackageDTO getPackagesByTrackingNumber(String trackingNumber) {
        validateTrackingNumber(trackingNumber);
        return packageRepository.findPackageByTrackingNumber(trackingNumber)
                .map(packageMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Package with tracking number " + trackingNumber +
                        " not found"));
    }

    private void validateFilters(UUID uuid) {
        if (uuid == null) {
            throw new IllegalArgumentException("Provide uuid. ");
        }
    }

    private void validateTrackingNumber(String trackingNumber) {
        String TRACKING_NUMBER_REGEX = "^TRK20\\d{2}\\d{9}$";
        if (trackingNumber == null || trackingNumber.isBlank()) {
            throw new IllegalArgumentException("Tracking number cannot be null or blank.");
        }

        if (!trackingNumber.matches(TRACKING_NUMBER_REGEX)) {
            throw new IllegalArgumentException("Invalid tracking number format.");
        }
    }

    private void validatePackageDimensions(UpdatePackageDimensionsRequestDTO request) {
        if (request == null) return;
        validatePositiveDimensions(request.weight(), request.length(), request.width(), request.height());
    }

    private void validateNewPackageRequest(PackageCreateRequest pack) {
        // Sprawdzamy null tylko dla obiektów (pack, weight, dimensions)
        if (pack == null || pack.getDimensions() == null || pack.getWeight() == null) {
            throw new IllegalArgumentException("Dimensions and weight are required");
        }

        DimensionsDTO dims = pack.getDimensions();

        // int z getDepth_length(), getWidth(), getHeight() jest automatycznie konwertowany (autoboxing) na Integer!
        validatePositiveDimensions(
                pack.getWeight(),
                dims.getDepth_length(),
                dims.getWidth(),
                dims.getHeight()
        );
    }

    private boolean hasAnyField(UpdatePackageDimensionsRequestDTO request) {
        return request.weight() != null || request.length() != null
                || request.width() != null || request.height() != null;
    }
}