package com.io.logiflow.packageConsumer;

import com.io.logiflow.packageConsumer.domain.dao.Package;
import com.io.logiflow.packageConsumer.domain.dao.Truck;
import com.io.logiflow.packageConsumer.infrastructure.service.ParcelPackingService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PackAppTest {
    public static void main(String[] args) {
        // 1. Tworzymy ciężarówkę (np. duży samochód dostawczy)
        // Wymiary w cm: szerokość 250, wysokość 220, długość 600
        // Ładowność: 3500.00 kg
        Truck truck = new Truck(250, 220, 600, new BigDecimal("3500.00"));

        int count = 100;

        // 2. Generujemy 100 losowych paczek
        List<Package> packageList = generateRandomBoxes(count);

        System.out.println("--- ROZPOCZĘCIE TESTU ---");
        System.out.println("Liczba paczek do spakowania: " + packageList.size());
        System.out.println("Maksymalna ładowność ciężarówki: " + truck.getMaxPayload() + " kg");
        System.out.println("-------------------------");

        // 3. Pomiar czasu (Start)
        long startTime = System.nanoTime();

        // Uruchomienie Twojego algorytmu
        ParcelPackingService packer = new ParcelPackingService();
        packer.pack(packageList, truck);

        // Pomiar czasu (Koniec)
        long endTime = System.nanoTime();
        long durationInMs = (endTime - startTime) / 1_000_000; // Konwersja na milisekundy

        // 4. Wyświetlenie wyników
        System.out.println("--- WYNIKI TESTU ---");
        System.out.println("Czas wykonania algorytmu: " + durationInMs + " ms");
        System.out.println("Spakowane paczki w aucie: " + truck.getPackedPackages().size() + " / " + count);
        System.out.println("Końcowa waga ładunku: " + truck + " kg");
        System.out.println("Pozostałe wolne przestrzenie (fragmentacja): " + truck.getEmptyCargoSpaces().size());
    }

    // Metoda pomocnicza do generowania losowych danych
    private static List<Package> generateRandomBoxes(int count) {
        List<Package> aPackages = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < count; i++) {

            // Losowe wymiary paczki (od 20 do 80 cm)
            int width = random.nextInt(61) + 20;
            int height = random.nextInt(61) + 20;
            int depth = random.nextInt(61) + 20;

            // Losowa waga (od 5.0 do 80.0 kg)
            double randomWeight = 5.0 + (75.0 * random.nextDouble());
            // Zaokrąglamy do 2 miejsc po przecinku
            BigDecimal weight = BigDecimal.valueOf(randomWeight).setScale(2, BigDecimal.ROUND_HALF_UP);

            aPackages.add(new Package(width, height, depth, weight));
        }
        return aPackages;
    }
}