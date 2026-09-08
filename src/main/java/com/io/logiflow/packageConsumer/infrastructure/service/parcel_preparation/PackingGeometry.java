package com.io.logiflow.packageConsumer.infrastructure.service.parcel_preparation;


import com.io.logiflow.packageConsumer.domain.dao.CargoSpace;
import com.io.logiflow.packageConsumer.domain.dao.Package;
import org.springframework.stereotype.Component;

@Component
public class PackingGeometry {

    private static final boolean[] ALLOWED_ROTATIONS = {false, true};

    //Check if the package fits in the space
    // Checks both orientations at a GIVEN point and returns the one that fits TIGHTER
    // (less wasted space in the area). Returns null if neither fits.
    // In case of a tie, it prefers the NON-rotated orientation (false)—why rotate if there’s no benefit?

    public Boolean pickBestRotation(Package aPackage, CargoSpace zone, int x, int y, int z) {
        boolean originalRotation = aPackage.isRotated();
        Boolean best = null;
        int bestWaste = Integer.MAX_VALUE;

        for (boolean rotated : ALLOWED_ROTATIONS) {
            aPackage.setRotated(rotated);

            if (!fitsWithinZoneBounds(x, y, z, aPackage, zone)) {
                continue;
            }

            int waste = wastedMargin(aPackage, zone);
            if (waste < bestWaste) {
                bestWaste = waste;
                best = rotated;
            }
        }
        if (best != null) {
            aPackage.setRotated(best);
        } else {
            aPackage.setRotated(originalRotation);
        }

        return best;
    }

    // The sum of the package's excess width and depth relative to the zone—the smaller this value, the tighter the fit.
    private int wastedMargin(Package b, CargoSpace zone) {
        int widthMargin = zone.getWidth() - b.getEffectiveWidth();
        int depthMargin = zone.getDepth() - b.getEffectiveDepth();
        return Math.max(widthMargin, depthMargin);
    }


    private boolean fitsWithinZoneBounds(int x, int y, int z, Package b, CargoSpace zone) {
        return x >= zone.getPositionX()
                && y >= zone.getPositionY()
                && z >= zone.getPositionZ()
                && x + b.getEffectiveWidth() <= zone.getPositionX() + zone.getWidth()
                && y + b.getHeight() <= zone.getPositionY() + zone.getHeight()
                && z + b.getEffectiveDepth() <= zone.getPositionZ() + zone.getDepth();
    }
}
