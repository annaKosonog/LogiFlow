package com.io.logiflow.packageConsumer.infrastructure.specification;

import com.io.logiflow.packageConsumer.domain.dao.Truck;
import com.io.logiflow.packageConsumer.domain.dto.request.TruckFilterDto;
import com.io.logiflow.packageConsumer.infrastructure.specification.utils.SpecificationUtils;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.UUID;

public class TruckSpecifications {

    public static Specification<Truck> buildSpecification(TruckFilterDto filter) {
        if (filter == null) {
            return (root, query, criteriaBuilder) -> null;
        }
        return Specification
                .where(hasNumberUUIDTruck(filter.getUuid()))
                .and(widthBetweenTruck(filter.getMinWidth(), filter.getMaxWidth()))
                .and(heightBetweenTruck(filter.getMinHeight(), filter.getMaxHeight()))
                .and(lengthBetweenTruck(filter.getMinLength(), filter.getMaxLength()))
                .and(currentWeightBetween(filter.getMinWeight(), filter.getMaxWeight()))
                .and(maxPayloadBetween(filter.getMinMaxPayload(), filter.getMaxMaxPayload()));
    }

    public static Specification<Truck> hasNumberUUIDTruck(UUID uuid) {
        return SpecificationUtils.equalsUuidIfPresent("uuid", uuid);
    }

    public static Specification<Truck> widthBetweenTruck(Integer minWidth, Integer maxWidth) {
        return SpecificationUtils.between("width", minWidth, maxWidth);
    }

    public static Specification<Truck> heightBetweenTruck(Integer minHeight, Integer maxHeight) {
        return SpecificationUtils.between("height", minHeight, maxHeight);
    }

    public static Specification<Truck> lengthBetweenTruck(Integer min, Integer max) {
        return SpecificationUtils.between("length", min, max);
    }

    public static Specification<Truck> maxPayloadBetween(BigDecimal min, BigDecimal max) {
        return SpecificationUtils.between("maxPayload", min, max);
    }

    public static Specification<Truck> currentWeightBetween(BigDecimal min, BigDecimal max) {
        return SpecificationUtils.between("currentWeight", min, max);
    }
}
