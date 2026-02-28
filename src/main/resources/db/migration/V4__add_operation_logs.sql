-- V4__add_operation_logs.sql

-- Tabla para registro de operaciones y auditoría
CREATE TABLE operation_logs (
    id BIGSERIAL PRIMARY KEY,
    operation_type VARCHAR(50) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id BIGINT NOT NULL,
    user_type VARCHAR(50),
    user_id BIGINT,
    description TEXT,
    metadata JSONB,
    ip_address VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Índices para optimizar consultas
CREATE INDEX idx_operation_logs_type ON operation_logs(operation_type);
CREATE INDEX idx_operation_logs_entity ON operation_logs(entity_type, entity_id);
CREATE INDEX idx_operation_logs_created ON operation_logs(created_at);
CREATE INDEX idx_operation_logs_user ON operation_logs(user_type, user_id);
