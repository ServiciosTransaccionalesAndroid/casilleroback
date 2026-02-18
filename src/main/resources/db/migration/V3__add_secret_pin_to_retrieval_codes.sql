-- V3__add_secret_pin_to_retrieval_codes.sql

ALTER TABLE retrieval_codes ADD COLUMN secret_pin VARCHAR(6);
