-- 1. Paczki, które zostały już ułożone w ciężarówce (posiadają współrzędne X, Y, Z)
-- Paczka 1: stoi w samym rogu (0,0,0), ma szerokość 30
INSERT INTO packages (tracking_number, width, height, depth, weight, coord_x, coord_y, coord_z, truck_id)
VALUES ('TRK-2026-A001', 30, 20, 40, 2.50, 0, 0, 0,1);

-- Paczka 2: stoi tuż obok Paczki 1 (X przesunięty o 30), ma szerokość 50
INSERT INTO packages (tracking_number, width, height, depth, weight, coord_x, coord_y, coord_z, truck_id)
VALUES ('TRK-2026-A002', 50, 20, 40, 5.00, 30, 0, 0,1);

-- Paczka 3: postawiona NA Paczce 1 (Y przesunięty w górę o wysokość pierwszej paczki, czyli 20)
INSERT INTO packages (tracking_number, width, height, depth, weight, coord_x, coord_y, coord_z, truck_id)
VALUES ('TRK-2026-A003', 30, 30, 40, 1.20, 0, 20, 0,2);


-- 2. Paczki nowo zarejestrowane, które dopiero czekają na algorytm pakowania (współrzędne są NULL)
INSERT INTO packages (tracking_number, width, height, depth, weight, coord_x, coord_y, coord_z, truck_id)
VALUES ('TRK-2026-B999', 100, 80, 120, 45.00, 0, 0, 0,2);

INSERT INTO packages (tracking_number, width, height, depth, weight, coord_x, coord_y, coord_z, truck_id)
VALUES ('TRK-2026-C444', 15, 15, 15, 0.50, 0, 0, 0,3);

-- Paczka zapakowana do ciężarówki o ID = 1, umieszczona na pozycji (0,0,0)
INSERT INTO packages (tracking_number, width, height, depth, weight, coord_x, coord_y, coord_z, truck_id)
VALUES ('TRK-2026-XYZ1', 40, 40, 40, 10.50, 0, 0, 0, 1);

-- Paczka zapakowana do tej samej ciężarówki (ID = 1), stojąca obok (X przesunięty o szerokość poprzedniej paczki, czyli 40)
INSERT INTO packages (tracking_number, width, height, depth, weight, coord_x, coord_y, coord_z, truck_id)
VALUES ('TRK-2026-XYZ2', 50, 30, 40, 15.00, 40, 0, 0, 1);

-- Paczka, która leży na magazynie (nie jest przypisana do żadnej ciężarówki -> truck_id oraz współrzędne są NULL)
INSERT INTO packages (tracking_number, width, height, depth, weight, coord_x, coord_y, coord_z, truck_id)
VALUES ('TRK-2026-MAG9', 20, 20, 20, 1.20, 0, 0, 0, 1);


-- 1. Początek: Cała ciężarówka jest pusta.
-- Zaczynamy od punktu (0,0,0). Ciężarówka ma wymiary: szerokość 240cm, wysokość 260cm, głębokość 600cm.
INSERT INTO cargo_spaces (position_x, position_y, position_z, width, height, depth, truck_id)
VALUES (0, 0, 0, 240, 260, 600,1);


-- 2. Stan po wrzuceniu pierwszej paczki w róg (0,0,0) o wymiarach 50x50x50.
-- Algorytm stworzył nowe, mniejsze "wolne przestrzenie" wokół tej paczki:

-- Wolna przestrzeń PO PRAWEJ STRONIE paczki (X rusza od 50, szerokość mniejsza o 50)
INSERT INTO cargo_spaces (position_x, position_y, position_z, width, height, depth, truck_id)
VALUES (50, 0, 0, 190, 260, 600,1);

-- Wolna przestrzeń NAD paczką (Y rusza od 50, wysokość mniejsza o 50)
INSERT INTO cargo_spaces (position_x, position_y, position_z, width, height, depth, truck_id)
VALUES (0, 50, 0, 50, 210, 600,1);

-- Wolna przestrzeń ZA paczką (Z rusza od 50, głębokość mniejsza o 50)
INSERT INTO cargo_spaces (position_x, position_y, position_z, width, height, depth, truck_id)
VALUES (0, 0, 50, 50, 50, 550,2);


-- 3. Mała, wąska szczelina ("półka"), która została gdzieś wysoko pod sufitem ciężarówki
-- Np. na samym końcu paki (Z=500), wysoko (Y=200), szeroka na 100cm, wysoka na 60cm, głęboka na 100cm.
INSERT INTO cargo_spaces (position_x, position_y, position_z, width, height, depth, truck_id)
VALUES (100, 200, 500, 100, 60, 100,2);