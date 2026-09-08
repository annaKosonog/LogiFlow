package com.io.logiflow.packageConsumer.infrastructure.service.parcel_preparation;

import org.springframework.stereotype.Component;
import com.io.logiflow.packageConsumer.domain.dao.Package;

import java.util.List;

@Component
public class CollisionDetector {
    public boolean overlapsWithLoadedPackages(Package candidate, List<Package> loadedPackages) {
        for (Package loaded : loadedPackages) {
            if (loaded.getUuid().equals(candidate.getUuid())) {
                continue;
            }
            if (boxesOverlap(candidate, loaded)) {
                return true;
            }
        }
        return false;
    }

    private boolean boxesOverlap(Package a, Package b) {
        boolean separatedOnX = a.getCoordX() + a.getEffectiveWidth() <= b.getCoordX()
                || b.getCoordX() + b.getEffectiveWidth() <= a.getCoordX();

        boolean separatedOnY = a.getCoordY() + a.getHeight() <= b.getCoordY()
                || b.getCoordY() + b.getHeight() <= a.getCoordY();

        boolean separatedOnZ = a.getCoordZ() + a.getEffectiveDepth() <= b.getCoordZ()
                || b.getCoordZ() + b.getEffectiveDepth() <= a.getCoordZ();

        return !(separatedOnX || separatedOnY || separatedOnZ);
    }
}
