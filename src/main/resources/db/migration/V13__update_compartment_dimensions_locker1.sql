-- V13__update_compartment_dimensions_locker1.sql

-- Agregar columnas de dimensiones a compartments
ALTER TABLE compartments ADD COLUMN IF NOT EXISTS width DECIMAL(10, 2);
ALTER TABLE compartments ADD COLUMN IF NOT EXISTS height DECIMAL(10, 2);
ALTER TABLE compartments ADD COLUMN IF NOT EXISTS depth DECIMAL(10, 2);

-- Actualizar dimensiones para compartimentos del Locker 1
-- Compartimentos 1,2,7,8,15,16: 33x23x45 (MEDIUM)
UPDATE compartments 
SET width = 33, height = 23, depth = 45, size = 'MEDIUM'
WHERE locker_id = 1 AND compartment_number IN (1, 2, 7, 8, 15, 16);

-- Compartimentos 4,5,6,12,13,14,20,21,22: 33x8x45 (S_SMALL)
UPDATE compartments 
SET width = 33, height = 8, depth = 45, size = 'S_SMALL'
WHERE locker_id = 1 AND compartment_number IN (4, 5, 6, 12, 13, 14, 20, 21, 22);

-- Compartimentos 9,10,11,17,18,19: 33x12x45 (M_SMALL)
UPDATE compartments 
SET width = 33, height = 12, depth = 45, size = 'M_SMALL'
WHERE locker_id = 1 AND compartment_number IN (9, 10, 11, 17, 18, 19);

-- Compartimentos 23,24: 33x23x45 (MEDIUM)
UPDATE compartments 
SET width = 33, height = 23, depth = 45, size = 'MEDIUM'
WHERE locker_id = 1 AND compartment_number IN (23, 24);

-- Deshabilitar compartimento 3 (no se utilizará)
UPDATE compartments 
SET status = 'FUERA_DE_SERVICIO', width = 0, height = 0, depth = 0
WHERE locker_id = 1 AND compartment_number = 3;
