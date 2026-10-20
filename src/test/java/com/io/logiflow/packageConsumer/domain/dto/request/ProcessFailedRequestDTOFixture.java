package com.io.logiflow.packageConsumer.domain.dto.request;

import com.io.logiflow.packageConsumer.infrastructure.dao.ProcessFailed;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProcessFailedRequestDTOFixture {
    public static ProcessFailed listOfFailuresContainsOneElement() {
        List<UUID> uuidList = new ArrayList<>();
        uuidList.add(UUID.fromString("3a2b1c4d-5e6f-4a7b-8c9d-0e1f2a3b4c5d"));
        return new ProcessFailed(1L, UUID.fromString("a1b2c3d4-e5f6-4789-a012-3456789abcde"), uuidList);
    }

    public static ProcessFailed onlyOneElementDoNotFits() {
        List<UUID> uuidList = new ArrayList<>();
        uuidList.add(UUID.fromString("9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d"));
        return new ProcessFailed(2L, UUID.fromString("173f5e17-c963-4ce9-8ebd-fa7f0f3bd940"), uuidList);
    }
}
