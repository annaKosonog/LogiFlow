package com.io.logiflow.packageConsumer.infrastructure.dto.response;

import java.util.List;
import java.util.UUID;

public record ProcessFailedResponseDto(UUID uuid, List<UUID> unpackedPackageUuids) {
}
