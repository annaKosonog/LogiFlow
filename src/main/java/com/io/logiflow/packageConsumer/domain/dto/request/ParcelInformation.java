package com.io.logiflow.packageConsumer.domain.dto.request;

import com.io.logiflow.packageConsumer.domain.dto.response.CoordinatesDTO;
import com.io.logiflow.packageConsumer.domain.dto.response.DimensionsDTO;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class ParcelInformation {
    @NotEmpty(message = "Parcel id cannot be empty")
    private final UUID parcelId;

    private final CoordinatesDTO position;
    private final DimensionsDTO dimension;

}
