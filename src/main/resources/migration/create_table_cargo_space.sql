CREATE TABLE IF NOT EXISTS cargo_spaces (
                             cargo_space_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                             uuid UUID NOT NULL UNIQUE DEFAULT gen_random_uuid(),

    -- Punkt początkowy wolnej przestrzeni (Współrzędne)
                             position_x INT NOT NULL,
                             position_y INT NOT NULL,
                             position_z INT NOT NULL,

    -- Wymiary wolnej przestrzeni
                             width INT NOT NULL,
                             height INT NOT NULL,
                             depth INT NOT NULL,

    -- Relacja ManyToOne do tabeli z ciężarówkami
                             truck_id BIGINT NOT NULL,

    -- Klucz obcy łączący przestrzeń z tabelą trucks
    -- ON DELETE CASCADE usunie przestrzenie, jeśli usuniesz całą ciężarówkę
                             CONSTRAINT fk_cargo_space_truck FOREIGN KEY (truck_id) REFERENCES trucks(truck_id)
);