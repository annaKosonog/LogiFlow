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
        uuidList.add(UUID.fromString("d81f3a6c-5e97-4b2d-8c04-f7a2b9e1d365"));
        return new PackingRequestDTO(uuidList, UUID.fromString("528e0004-a6f9-4018-bcba-87e20ad08606"));
    }

    public static PackingRequestDTO multiPacked() {
        List<UUID> uuidList = new ArrayList<>();
        uuidList.add(UUID.fromString("7f3a2b1c-9d84-4e6f-b521-8a7c3d2e1f90"));
        uuidList.add(UUID.fromString("9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d"));
        return new PackingRequestDTO(uuidList, UUID.fromString("c771f079-fb74-4811-b491-e78673c41e0d"));
    }

    public static PackingRequestDTO onePackageIsNotSave() {
        List<UUID> uuidList = new ArrayList<>();
        uuidList.add(UUID.fromString("9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d"));
        uuidList.add(UUID.fromString("7f3a2b1c-9d84-4e6f-b521-8a7c3d2e1f90"));
        return new PackingRequestDTO(uuidList, UUID.fromString("c771f079-fb74-4811-b491-e78673c41e0d"));
    }


    public static PackingRequestDTO randomUUIDTruck() {
        List<UUID> uuidList = new ArrayList<>();
        uuidList.add(thirdBoxPackToTruck().getUuid());
        return new PackingRequestDTO(uuidList, UUID.randomUUID());
    }

    public static PackingRequestDTO emptyList() {
        return new PackingRequestDTO(List.of(), UUID.fromString("3a2b1c4d-5e6f-4a7b-8c9d-0e1f2a3b4c5d"));
    }

    public static PackingRequestDTO packageSavedDb() {
        List<UUID> uuidList = new ArrayList<>();
        uuidList.add(UUID.fromString("f4f54dbe-0109-47fe-ba02-b07a2836dab3"));
        return new PackingRequestDTO(uuidList, UUID.fromString("3b4bc480-127b-4163-a1a3-642e48d091b6"));
    }


}
