-- V11__add_compartment_door_and_condition.sql
-- Agregar estado de puerta y condición física a compartimentos

ALTER TABLE compartments 
ADD COLUMN door_state VARCHAR(20) NOT NULL DEFAULT 'CERRADO',
ADD COLUMN physical_condition VARCHAR(30) NOT NULL DEFAULT 'BUEN_ESTADO';

-- Crear índices para consultas rápidas
CREATE INDEX idx_compartments_door_state ON compartments(door_state);
CREATE INDEX idx_compartments_physical_condition ON compartments(physical_condition);

-- Comentarios
COMMENT ON COLUMN compartments.door_state IS 'Estado de la puerta: CERRADO, ABIERTO';
COMMENT ON COLUMN compartments.physical_condition IS 'Condición física: BUEN_ESTADO, MAL_ESTADO, REQUIERE_MANTENIMIENTO';
