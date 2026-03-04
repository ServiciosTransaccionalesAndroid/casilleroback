-- V14__migrate_old_small_to_s_small.sql

-- Actualizar compartimentos que NO están en el locker 1 o que no tienen dimensiones definidas
-- Convertir SMALL antiguos a S_SMALL por defecto
UPDATE compartments 
SET size = 'S_SMALL' 
WHERE size = 'SMALL' 
  AND (locker_id != 1 OR width IS NULL);
