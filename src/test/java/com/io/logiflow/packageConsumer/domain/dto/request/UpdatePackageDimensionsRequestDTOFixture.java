package com.io.logiflow.packageConsumer.domain.dto.request;

import java.math.BigDecimal;

public class UpdatePackageDimensionsRequestDTOFixture {

    public static UpdatePackageDimensionsRequestDTO fieldWeightToUpdate() {
      return  new UpdatePackageDimensionsRequestDTO(new BigDecimal(750), null, null, null);
    }

    public static UpdatePackageDimensionsRequestDTO secondFieldWeightToUpdate() {
        return  new UpdatePackageDimensionsRequestDTO(new BigDecimal("127"), null, null, null);
    }

    public static UpdatePackageDimensionsRequestDTO fieldWeightAndWidthToUpdate() {
        return  new UpdatePackageDimensionsRequestDTO(new BigDecimal("12.95"), null, 45, null);
    }

    public static UpdatePackageDimensionsRequestDTO updateAllFields(){
        return new UpdatePackageDimensionsRequestDTO( new BigDecimal("87"), 120, 15,12);
    }

    public static UpdatePackageDimensionsRequestDTO negativeWeightToUpdate(){
        return new UpdatePackageDimensionsRequestDTO(new BigDecimal("-12.4"), null, null, null);
    }

    public static UpdatePackageDimensionsRequestDTO wrongTypeWeightToUpdate(){
        return new UpdatePackageDimensionsRequestDTO(new BigDecimal("aac"), null, null,null);
    }

    public static UpdatePackageDimensionsRequestDTO emptyFieldsToUpdate(){
        return new UpdatePackageDimensionsRequestDTO(null, null, null, null);
    }
}

