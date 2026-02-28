-- V5__add_performance_indexes.sql

-- Índices compuestos para mejorar performance de queries de reportes

-- Índice para consultas de depósitos por fecha y compartimento
CREATE INDEX idx_deposits_timestamp_compartment ON deposits(deposit_timestamp, compartment_id);

-- Índice para consultas de retiros por fecha
CREATE INDEX idx_retrievals_timestamp ON retrievals(retrieval_timestamp);

-- Índice para códigos de retiro no usados y expirados
CREATE INDEX idx_retrieval_codes_used_expires ON retrieval_codes(used, expires_at);

-- Índice para compartimentos por locker y estado
CREATE INDEX IF NOT EXISTS idx_compartments_locker_status ON compartments(locker_id, status);
