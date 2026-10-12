package com.io.logiflow.packageConsumer.domain.dto.response;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PackingResultDTOFixture {

    public static PackingResultDTO firstAddPacking() {
        List<UUID> uuidList = new ArrayList<>();
        uuidList.add(UUID.fromString("3a2b1c4d-5e6f-4a7b-8c9d-0e1f2a3b4c5d"));
        return new PackingResultDTO(UUID.fromString("528e0004-a6f9-4018-bcba-87e20ad08606"), uuidList, null);
    }
}
