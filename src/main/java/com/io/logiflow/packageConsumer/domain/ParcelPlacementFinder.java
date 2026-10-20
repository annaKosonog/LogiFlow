package com.io.logiflow.packageConsumer.domain;

import com.io.logiflow.packageConsumer.infrastructure.dao.Package;
import com.io.logiflow.packageConsumer.infrastructure.dto.response.CoordinatesDTO;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ParcelPlacementFinder {

    public Optional<CoordinatesDTO> findFreePosition(CargoGrid grid, Package pkg,
                                                     int truckWidth, int truckHeight, int truckDepth) {
        Optional<CoordinatesDTO> position = scan(grid, pkg, truckWidth, truckHeight, truckDepth, false);
        if (position.isPresent()) {
            pkg.setRotated(false);
            return position;
        }
        position = scan(grid, pkg, truckWidth, truckHeight, truckDepth, true);
        if (position.isPresent()) {
            pkg.setRotated(true);
            return position;
        }
        return Optional.empty(); // rotated pozostaje bez zmian — nie mutujemy obiektu, skoro się nie udało
    }

    private Optional<CoordinatesDTO> scan(CargoGrid grid, Package pkg,
                                          int truckWidth, int truckHeight, int truckDepth,
                                          boolean rotated) {
        int w = rotated ? pkg.getDepth() : pkg.getWidth();
        int h = pkg.getHeight();
        int d = rotated ? pkg.getWidth() : pkg.getDepth();
        // reszta bez zmian — liczymy w/d lokalnie, nie mutując pkg.rotated w trakcie skanowania
        for (int z = 0; z <= truckDepth - d; z++) {
            for (int y = 0; y <= truckHeight - h; y++) {
                for (int x = 0; x <= truckWidth - w; x++) {
                    if (grid.isFree(x, y, z, w, h, d)) {
                        return Optional.of(new CoordinatesDTO(x, y, z));
                    }
                }
            }
        }
        return Optional.empty();
    }
}
