package com.io.logiflow.packageConsumer.infrastructure.service;

import com.io.logiflow.packageConsumer.domain.dao.Package;
import com.io.logiflow.packageConsumer.domain.dto.DimensionsDTO;
import com.io.logiflow.packageConsumer.domain.dto.PackageCreateRequest;
import com.io.logiflow.packageConsumer.domain.dto.PackageDTO;
import com.io.logiflow.packageConsumer.domain.mapper.PackageCreateRequestMapper;
import com.io.logiflow.packageConsumer.domain.mapper.PackageMapper;
import com.io.logiflow.packageConsumer.domain.repository.PackageRepository;
import com.io.logiflow.packageConsumer.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

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

    public Page<PackageDTO> getPackages(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Package> packPage = packageRepository.findAll(pageable);
        return packPage.map(packageMapper::toDTO);
    }

    @Transactional
    public PackageDTO updatePackageFields(UUID packId, Map<String, Object> fields) { //waga, wymiary, adres dostawy
        if (fields == null || fields.isEmpty()) {
            throw new IllegalArgumentException("At least one field must be provided");
        }

        Package existingPackage = packageRepository.findPackageByUuid(packId)
                .orElseThrow(() -> new ResourceNotFoundException("Package not found"));

        validatePositiveNumericFields(fields, "width", "height", "depth", "weight");

        // Sprawdzamy czy w JSONIE przesłano konkretne klucze i bezpiecznie je przepisujemy
        if (fields.containsKey("width")) {
            existingPackage.setWidth(((Number) fields.get("width")).intValue());
        }
        if (fields.containsKey("height")) {
            existingPackage.setHeight(((Number) fields.get("height")).intValue());
        }
        if (fields.containsKey("depth")) {
            existingPackage.setDepth(((Number) fields.get("depth")).intValue());
        }
        if (fields.containsKey("weight")) {
            Object weightValue = fields.get("weight");
            existingPackage.setWeight(new BigDecimal(weightValue.toString()));
        }
        Package saved = packageRepository.save(existingPackage);
        return packageMapper.toDTO(saved);
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

    public void deleteAllPackage() {
        packageRepository.deleteAll();
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

    private void validatePositiveNumericFields(Map<String, Object> fields, String... fieldNames) {
        for (String fieldName : fieldNames) {
            if (!fields.containsKey(fieldName)) {
                continue;
            }
            Object value = fields.get(fieldName);
            BigDecimal numericValue;
            try {
                numericValue = new BigDecimal(value.toString());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(fieldName + " must be a valid number");
            }
            if (numericValue.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException(fieldName + " must be positive");
            }
        }
    }

    private void validateNewPackageRequest(PackageCreateRequest pack) {
        if (pack == null || pack.getDimensions() == null || pack.getWeight() == null) {
            throw new IllegalArgumentException("Dimensions and weight are required");
        }

        DimensionsDTO dimensions = pack.getDimensions();

        if (dimensions.getWidth() <= 0) {
            throw new IllegalArgumentException("Width must be positive");
        }
        if (dimensions.getHeight() <= 0) {
            throw new IllegalArgumentException("Height must be positive");
        }
        if (dimensions.getDepth_length() <= 0) {
            throw new IllegalArgumentException("Depth must be positive");
        }
        if (pack.getWeight().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Weight must be positive");
        }
    }
}