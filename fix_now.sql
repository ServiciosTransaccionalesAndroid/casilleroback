-- EJECUTAR EN RAILWAY POSTGRESQL AHORA
-- Esto arreglará el error de Flyway

DELETE FROM flyway_schema_history WHERE version = '6';
