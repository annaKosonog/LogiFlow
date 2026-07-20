-- 1. Paczki, które zostały już ułożone w ciężarówce (posiadają współrzędne X, Y, Z)
-- Paczka 1: stoi w samym rogu (0,0,0), ma szerokość 30
INSERT INTO packages (uuid, tracking_number, width, height, depth, weight, coord_x, coord_y, coord_z, truck_id)
VALUES ('123e4567-e89b-12d3-a456-426614174000', 'TRK2026000000001', 30, 20, 40, 2.50, 0, 0, 0, 1);

-- Paczka 2: stoi tuż obok Paczki 1 (X przesunięty o 30), ma szerokość 50
INSERT INTO packages (uuid, tracking_number, width, height, depth, weight, coord_x, coord_y, coord_z, truck_id)
VALUES ('a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d', 'TRK2026000000002', 50, 20, 40, 5.00, 30, 0, 0, 1);

--Paczka 3: postawiona NA Paczce 1 (Y przesunięty w górę o wysokość pierwszej paczki, czyli 20)
INSERT INTO packages (uuid, tracking_number, width, height, depth, weight, coord_x, coord_y, coord_z, truck_id)
VALUES ('f47ac10b-58cc-4372-a567-0e02b2c3d479', 'TRK2026000000003', 30, 30, 40, 1.20, 0, 20, 0, 2);


-- 2. Paczki nowo zarejestrowane, które dopiero czekają na algorytm pakowania (współrzędne są NULL)
INSERT INTO packages (uuid, tracking_number, width, height, depth, weight, coord_x, coord_y, coord_z, truck_id)
VALUES ('7c9e0d12-5b1d-407e-8c43-1bc2d3f4a5b6', 'TRK2026000000004', 100, 80, 120, 45.00, 0, 0, 0, 2);

INSERT INTO packages (uuid, tracking_number, width, height, depth, weight, coord_x, coord_y, coord_z, truck_id)
VALUES ('b17a8d83-78b0-4875-ae78-c83d14e17312', 'TRK2026000000005', 15, 15, 15, 0.50, 0, 0, 0, 3);

-- Paczka zapakowana do ciężarówki o ID = 1, umieszczona na pozycji (0,0,0)
INSERT INTO packages (uuid, tracking_number, width, height, depth, weight, coord_x, coord_y, coord_z, truck_id)
VALUES ('f4f54dbe-0109-47fe-ba02-b07a2836dab3', 'TRK2026000000006', 40, 40, 40, 10.50, 0, 0, 0, null);

-- Paczka zapakowana do tej samej ciężarówki (ID = 2), stojąca obok (X przesunięty o szerokość poprzedniej paczki, czyli 40)
INSERT INTO packages (uuid, tracking_number, width, height, depth, weight, coord_x, coord_y, coord_z, truck_id)
VALUES ('e6d49a74-99f7-4e85-b325-3e2cd906d176', 'TRK2026000000007', 50, 30, 40, 15.00, 0, 0, 0, null);

-- Paczka, która leży na magazynie (nie jest przypisana do żadnej ciężarówki -> truck_id oraz współrzędne są NULL)
INSERT INTO packages (uuid, tracking_number, width, height, depth, weight, coord_x, coord_y, coord_z, truck_id)
VALUES ('b3eaa788-1c0e-4be8-8591-960d6616e7dc', 'TRK2026000000008', 20, 20, 20, 1.20, 0, 0, 0, null);


-- 1. Początek: Cała ciężarówka jest pusta.
-- Zaczynamy od punktu (0,0,0). Ciężarówka ma wymiary: szerokość 240cm, wysokość 260cm, głębokość 600cm.
INSERT INTO cargo_spaces (uuid, position_x, position_y, position_z, width, height, depth, truck_id)
VALUES ('a636c79e-96cb-4bfa-83db-af248e7eb617', 0, 0, 0, 240, 260, 600, 1);


-- 2. Stan po wrzuceniu pierwszej paczki w róg (0,0,0) o wymiarach 50x50x50.
-- Algorytm stworzył nowe, mniejsze "wolne przestrzenie" wokół tej paczki:

-- Wolna przestrzeń PO PRAWEJ STRONIE paczki (X rusza od 50, szerokość mniejsza o 50)
INSERT INTO cargo_spaces (uuid, position_x, position_y, position_z, width, height, depth, truck_id)
VALUES ('db3ae263-33ca-415a-ae83-bbf07a170a17', 50, 0, 0, 190, 260, 600, 2);

-- Wolna przestrzeń NAD paczką (Y rusza od 50, wysokość mniejsza o 50, X i Z dla całej szerokości/głębokości ciężarówki)
INSERT INTO cargo_spaces (uuid, position_x, position_y, position_z, width, height, depth, truck_id)
VALUES ('72c07fee-2e02-4475-b1d6-8fa3775d9886', 0, 50, 0, 240, 210, 600, 3);

-- Wolna przestrzeń ZA paczką (Z rusza od 50, głębokość mniejsza o 50)
INSERT INTO cargo_spaces (uuid, position_x, position_y, position_z, width, height, depth, truck_id)
VALUES ('853a8468-97ea-444e-b9ff-c8c8709742f8', 0, 0, 50, 240, 260, 550, 4);


-- 3. Mała, wąska szczelina ("półka"), która została gdzieś wysoko pod sufitem ciężarówkió
-- Np. na samym końcu paki (Z=500), wysoko (Y=200), szeroka na 100cm, wysoka na 60cm, głęboka na 100cm.
INSERT INTO cargo_spaces (uuid, position_x, position_y, position_z, width, height, depth, truck_id)
VALUES ('23f018ef-44d4-473b-8f36-2a892f635098', 100, 200, 500, 100, 60, 100, 5);