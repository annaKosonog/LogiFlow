package com.io.logiflow.packageConsumer.infrastructure.specification.utils;

import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class SpecificationUtils {

    public static <T, Y extends Comparable<? super Y>> Specification<T> between(
            String field, Y min, Y max) {
        return (root, query, cb) -> {
            if (min != null && max != null) {
                return cb.between(root.get(field), min, max);
            } else if (min != null) {
                return cb.greaterThanOrEqualTo(root.get(field), min);
            } else if (max != null) {
                return cb.lessThanOrEqualTo(root.get(field), max);
            }
            return null;
        };
    }

    public static <T> Specification<T> equalsIfPresent(String field, String value) {
        return (root, query, criteriaBuilder) ->
                (value == null || value.isBlank())
                        ? null
                        : criteriaBuilder.like(
                        criteriaBuilder.lower(root.get(field)),
                        "%" + value.toLowerCase() + "%"
                );
    }

    public static <T> Specification<T> equalsUuidIfPresent(String field, UUID value) {
        return (root, query, criteriaBuilder) ->
                (value == null)
                        ? null
                        : criteriaBuilder.equal(root.get(field), value);
    }
}
