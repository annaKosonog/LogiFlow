INSERT INTO trucks (uuid,width_w, height_h, length_l, max_payload, current_weight)
VALUES ('493afc07-d433-4cfa-8947-0c6100c127df',240, 260, 600, 3500.00, 0.00);
-- Załóżmy, że ta ciężarówka otrzymała truck_id = 1

-- 1. Mały kurierski Bus / Van (np. Mercedes Sprinter)
-- Wymiary: szer: 170cm, wys: 190cm, dł: 430cm | Ładowność: 1200kg
INSERT INTO trucks (uuid,width_w, height_h, length_l, max_payload, current_weight)
VALUES ('2b8ecd55-c636-4ba0-8192-42c9e8a14355',170, 190, 430, 1200.00, 0.00);


-- 2. Samochód ciężarowy typu "Solówka" z windą (często spotykany w dystrybucji lokalnej)
-- Wymiary: szer: 245cm, wys: 250cm, dł: 750cm | Ładowność: 8500kg
INSERT INTO trucks (uuid,width_w, height_h, length_l, max_payload, current_weight)
VALUES ('f80fce2d-76ab-42b5-b489-169a1e864b2c',245, 250, 750, 8500.00, 0.00);


-- 3. Klasyczny ciągnik siodłowy z naczepą standardową (TIR / Mega)
-- Wymiary: szer: 248cm, wys: 300cm, dł: 1362cm | Ładowność: 24000kg
INSERT INTO trucks (uuid,width_w, height_h, length_l, max_payload, current_weight)
VALUES ('a21d7781-c861-4d0a-9f87-2084c23f9cee',248, 300, 1362, 24000.00, 0.00);


-- 4. Małe auto miejskie do szybkich paczek (np. Dacia Dokker Van)
-- Wymiary: szer: 140cm, wys: 120cm, dł: 190cm | Ładowność: 600kg
INSERT INTO trucks (uuid,width_w, height_h, length_l, max_payload, current_weight)
VALUES ('bb541dbd-3206-4f7e-b23c-ae6f9af78db5',140, 120, 190, 600.00, 0.00);


-- 5. Ciężarówka typu Chłodnia (ma grubsze ściany izolacyjne, stąd nieco mniejsza szerokość w środku)
-- Wymiary: szer: 240cm, wys: 260cm, dł: 1340cm | Ładowność: 22500kg
INSERT INTO trucks (uuid,width_w, height_h, length_l, max_payload, current_weight)
VALUES ('3b4bc480-127b-4163-a1a3-642e48d091b6',240, 260, 1340, 22500.00, 0.00);