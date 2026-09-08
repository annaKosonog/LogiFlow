-- 1. Tworzymy sekwencję, która będzie odliczać numery paczek
CREATE SEQUENCE IF NOT EXISTS tracking_number_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS packages (
                          package_id BIGSERIAL PRIMARY KEY,
                          uuid UUID NOT NULL UNIQUE DEFAULT gen_random_uuid(),
                          tracking_number VARCHAR(50) NOT NULL UNIQUE,

    -- Wymiary paczki
                          width INT NOT NULL,
                          height INT NOT NULL,
                          depth INT NOT NULL,
                          weight DECIMAL(10, 2) NOT NULL,

    -- Współrzędne położenia paczki (wewnątrz ciężarówki)
                          coord_x INT DEFAULT NULL,
                          coord_y INT DEFAULT NULL,
                          coord_z INT DEFAULT NULL,
                          rotated BOOLEAN NOT NULL DEFAULT false,
                          packing_order INT DEFAULT 0,

    -- NOWE POLA I RELACJE:
                          truck_id BIGINT DEFAULT NULL, -- Link do tabeli trucks (odpowiednik Twojego TruckId)
                          version BIGINT NOT NULL DEFAULT 0,

    -- Definicja klucza obcego spiętego z tabelą trucks
                          CONSTRAINT fk_packages_truck FOREIGN KEY (truck_id)
                          REFERENCES trucks(truck_id) ON DELETE SET NULL
);