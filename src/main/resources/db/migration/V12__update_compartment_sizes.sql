-- Actualizar tamaños de casilleros con dimensiones reales

-- SMALL (33x12x45): casilleros 9,10,11,17,18,19
UPDATE compartments SET size = 'SMALL' WHERE compartment_number IN (9,10,11,17,18,19);

-- MEDIUM (33x18x45): casilleros 4,5,6,12,13,14,20,21,22
UPDATE compartments SET size = 'MEDIUM' WHERE compartment_number IN (4,5,6,12,13,14,20,21,22);

-- LARGE (33x23x45): casilleros 1,2,7,8,15,16
UPDATE compartments SET size = 'LARGE' WHERE compartment_number IN (1,2,7,8,15,16);

-- EXTRA_LARGE (33x40x45): casillero 3
UPDATE compartments SET size = 'EXTRA_LARGE' WHERE compartment_number = 3;

-- JUMBO (40x49x45): casilleros 23,24
UPDATE compartments SET size = 'JUMBO' WHERE compartment_number IN (23,24);
