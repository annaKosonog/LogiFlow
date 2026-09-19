package com.io.logiflow.packageConsumer.domain.dto.request;

import com.io.logiflow.packageConsumer.domain.dto.response.CoordinatesDTO;
import com.io.logiflow.packageConsumer.domain.dto.response.DimensionsDTO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class ParcelInformation {
    private final UUID parcelId;
    private final CoordinatesDTO position;
    private final DimensionsDTO dimension;

}
