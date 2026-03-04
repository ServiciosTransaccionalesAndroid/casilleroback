-- V16__add_plain_pin_to_couriers.sql

-- Agregar columna para guardar el PIN sin encriptar
ALTER TABLE couriers ADD COLUMN plain_pin VARCHAR(10);
