package com.io.logiflow.packageConsumer.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProcessFailedRequestDto {
   private List<UUID> unpackedPackageUuids;
}
