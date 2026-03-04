-- V14__migrate_old_small_to_s_small.sql

-- Actualizar todos los compartimentos con SMALL a S_SMALL
-- (asumiendo que los SMALL antiguos son del tipo más pequeño)
UPDATE compartments 
SET size = 'S_SMALL' 
WHERE size = 'SMALL';
