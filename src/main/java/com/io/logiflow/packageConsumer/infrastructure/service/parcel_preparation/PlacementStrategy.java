package com.io.logiflow.packageConsumer.infrastructure.service.parcel_preparation;

import com.io.logiflow.packageConsumer.domain.dao.CargoSpace;
import com.io.logiflow.packageConsumer.domain.dao.Package;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
public class PlacementStrategy {

    public List<int[]> generateCandidatePoints(CargoSpace zone, List<Package> loadedPackages) {
        List<int[]> candidates = new ArrayList<>();
        candidates.add(new int[]{zone.getPositionX(), zone.getPositionY(), zone.getPositionZ()});

        for (Package p : loadedPackages) {
            if (!isWithinZone(p, zone)) {
                continue; // interesują nas tylko paczki leżące w TEJ konkretnej strefie
            }
            candidates.add(new int[]{p.getCoordX() + p.getEffectiveWidth(), p.getCoordY(), p.getCoordZ()});
            candidates.add(new int[]{p.getCoordX(), p.getCoordY() + p.getHeight(), p.getCoordZ()});
            candidates.add(new int[]{p.getCoordX(), p.getCoordY(), p.getCoordZ() + p.getEffectiveDepth()});
        }
        return candidates;
    }


    private boolean isWithinZone(Package p, CargoSpace zone) {
        return p.getCoordX() >= zone.getPositionX() && p.getCoordX() < zone.getPositionX() + zone.getWidth()
                && p.getCoordY() >= zone.getPositionY() && p.getCoordY() < zone.getPositionY() + zone.getHeight()
                && p.getCoordZ() >= zone.getPositionZ() && p.getCoordZ() < zone.getPositionZ() + zone.getDepth();
    }

    public void sortedPack(List<Package> boxesToPack) {
        boxesToPack.sort(
                Comparator.comparingInt((Package b) -> b.getWidth() * b.getHeight() * b.getDepth())
                        .reversed()
                        .thenComparing(Package::getWeight, Comparator.reverseOrder())
        );
    }

    public record Point(int x, int y, int z){

    }


}


//todo logika pakowania jedenj [aczaki