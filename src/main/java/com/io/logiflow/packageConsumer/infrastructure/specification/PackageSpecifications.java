package com.io.logiflow.packageConsumer.infrastructure.specification;

import com.io.logiflow.packageConsumer.domain.dao.Package;
import com.io.logiflow.packageConsumer.domain.dto.request.PackageFilterDTO;
import com.io.logiflow.packageConsumer.infrastructure.specification.utils.SpecificationUtils;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.UUID;

public class PackageSpecifications {

    public static Specification<Package> buildSpecification(PackageFilterDTO filter) {
        if (filter == null) {
            return (root, query, criteriaBuilder) -> null;
        }
        return Specification
                .where(hasNumberUUIDPackage(filter.getUuid()))
                .and(hasTrackingNumberPackage(filter.getTrackingNumber()))
                .and(weightBetweenPackage(filter.getMinWeight(), filter.getMaxWeight()))
                .and(depthBetweenPackage(filter.getMinDepth(), filter.getMaxDepth()))
                .and(heightBetweenPackage(filter.getMinHeight(), filter.getMaxHeight()))
                .and(widthBetweenPackage(filter.getMinWidth(), filter.getMaxWidth()));
    }

    public static Specification<Package> hasTrackingNumberPackage(String trackingNumber) {
        return SpecificationUtils.equalsIfPresent("trackingNumber", trackingNumber);
    }

    public static Specification<Package> hasNumberUUIDPackage(UUID uuid) {
        return SpecificationUtils.equalsUuidIfPresent("uuid", uuid);
    }

    public static Specification<Package> weightBetweenPackage(BigDecimal minWeight, BigDecimal maxWeight) {
        return SpecificationUtils.between("weight", minWeight, maxWeight);
    }

    public static Specification<Package> depthBetweenPackage(Integer minDepth, Integer maxDepth) {
        return SpecificationUtils.between("depth", minDepth, maxDepth);
    }

    public static Specification<Package> heightBetweenPackage(Integer minHeight, Integer maxHeight) {
        return SpecificationUtils.between("height", minHeight, maxHeight);
    }

    public static Specification<Package> widthBetweenPackage(Integer minWidth, Integer maxWidth) {
        return SpecificationUtils.between("width", minWidth, maxWidth);
    }
}
