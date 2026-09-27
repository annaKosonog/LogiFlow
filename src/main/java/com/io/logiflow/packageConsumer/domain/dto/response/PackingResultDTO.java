package com.io.logiflow.packageConsumer.domain.dto.response;

import java.util.List;
import java.util.UUID;

public record PackingResultDTO(
        UUID truckId,
        List<UUID> packedPackageUuids,
        UUID processFailedUuid
) {
}
