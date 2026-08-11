package com.io.logiflow.packageConsumer.domain.dto.request;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.io.logiflow.packageConsumer.domain.dao.PackageFixture.thirdBoxPackToTruck;

public class PackingRequestDTOFixture {
    public static PackingRequestDTO firstPackingToPack() {
        List<UUID> uuidList = new ArrayList<>();
        uuidList.add(UUID.fromString("3a2b1c4d-5e6f-4a7b-8c9d-0e1f2a3b4c5d"));
        return new PackingRequestDTO(uuidList, UUID.fromString("528e0004-a6f9-4018-bcba-87e20ad08606"));
    }

    public static PackingRequestDTO secondPackingToPack() {
        List<UUID> uuidList = new ArrayList<>();
        uuidList.add(UUID.fromString("9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d"));
        return new PackingRequestDTO(uuidList, UUID.fromString("c771f079-fb74-4811-b491-e78673c41e0d"));
    }

    public static PackingRequestDTO thirdPackingToPack() {
        List<UUID> uuidList = new ArrayList<>();
        uuidList.add(thirdBoxPackToTruck().getUuid());
        return new PackingRequestDTO(uuidList, UUID.fromString("528e0004-a6f9-4018-bcba-87e20ad08606"));
    }

    public static PackingRequestDTO randomUUIDTruck() {
        List<UUID> uuidList = new ArrayList<>();
        uuidList.add(thirdBoxPackToTruck().getUuid());
        return new PackingRequestDTO(uuidList, UUID.randomUUID());
    }

    public static PackingRequestDTO emptyList() {
        return new PackingRequestDTO(List.of(), UUID.fromString("3a2b1c4d-5e6f-4a7b-8c9d-0e1f2a3b4c5d"));
    }


}
