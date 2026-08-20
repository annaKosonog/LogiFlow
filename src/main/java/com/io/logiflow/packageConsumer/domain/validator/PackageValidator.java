package com.io.logiflow.packageConsumer.domain.validator;

import java.util.UUID;

public  final class PackageValidator {

    private PackageValidator() {
    }

    public static void validateTrackingNumber(String trackingNumber) {
        String TRACKING_NUMBER_REGEX = "^TRK20\\d{2}\\d{9}$";
        if (trackingNumber == null || trackingNumber.isBlank()) {
            throw new IllegalArgumentException("Tracking number cannot be null or blank.");
        }

        if (!trackingNumber.matches(TRACKING_NUMBER_REGEX)) {
            throw new IllegalArgumentException("Invalid tracking number format.");
        }
    }

    public static void validateFilters(UUID uuid) {
        if (uuid == null) {
            throw new IllegalArgumentException("Provide uuid. ");
        }
    }

}
