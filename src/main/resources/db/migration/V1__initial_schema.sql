-- V1__initial_schema.sql

-- Tabla de lockers
CREATE TABLE lockers (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    location VARCHAR(200) NOT NULL,
    address VARCHAR(300),
    latitude DECIMAL(10, 7),
    longitude DECIMAL(10, 7),
    status VARCHAR(20) NOT NULL,
    total_compartments INTEGER,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

-- Tabla de compartimentos
CREATE TABLE compartments (
    id BIGSERIAL PRIMARY KEY,
    locker_id BIGINT NOT NULL,
    compartment_number INTEGER NOT NULL,
    size VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    sensor_readings JSONB,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    FOREIGN KEY (locker_id) REFERENCES lockers(id) ON DELETE CASCADE
);

-- Tabla de paquetes
CREATE TABLE packages (
    id BIGSERIAL PRIMARY KEY,
    tracking_number VARCHAR(50) NOT NULL UNIQUE,
    recipient_name VARCHAR(200) NOT NULL,
    recipient_phone VARCHAR(20) NOT NULL,
    recipient_email VARCHAR(100),
    width DECIMAL(10, 2),
    height DECIMAL(10, 2),
    depth DECIMAL(10, 2),
    weight DECIMAL(10, 2),
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

-- Tabla de mensajeros
CREATE TABLE couriers (
    id BIGSERIAL PRIMARY KEY,
    employee_id VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(200) NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(100),
    pin VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

-- Tabla de depósitos
CREATE TABLE deposits (
    id BIGSERIAL PRIMARY KEY,
    package_id BIGINT NOT NULL,
    compartment_id BIGINT NOT NULL,
    courier_id BIGINT NOT NULL,
    deposit_timestamp TIMESTAMP NOT NULL,
    photo_url VARCHAR(500),
    created_at TIMESTAMP NOT NULL,
    FOREIGN KEY (package_id) REFERENCES packages(id),
    FOREIGN KEY (compartment_id) REFERENCES compartments(id),
    FOREIGN KEY (courier_id) REFERENCES couriers(id)
);

-- Tabla de códigos de retiro
CREATE TABLE retrieval_codes (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(10) NOT NULL UNIQUE,
    deposit_id BIGINT NOT NULL,
    generated_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    used BOOLEAN NOT NULL DEFAULT false,
    used_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    FOREIGN KEY (deposit_id) REFERENCES deposits(id)
);

-- Tabla de retiros
CREATE TABLE retrievals (
    id BIGSERIAL PRIMARY KEY,
    deposit_id BIGINT NOT NULL,
    retrieval_code_id BIGINT NOT NULL,
    retrieval_timestamp TIMESTAMP NOT NULL,
    photo_url VARCHAR(500),
    created_at TIMESTAMP NOT NULL,
    FOREIGN KEY (deposit_id) REFERENCES deposits(id),
    FOREIGN KEY (retrieval_code_id) REFERENCES retrieval_codes(id)
);

-- Tabla de historial de estados
CREATE TABLE status_history (
    id BIGSERIAL PRIMARY KEY,
    compartment_id BIGINT NOT NULL,
    previous_state VARCHAR(20) NOT NULL,
    current_state VARCHAR(20) NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    sensor_readings JSONB,
    created_at TIMESTAMP NOT NULL,
    FOREIGN KEY (compartment_id) REFERENCES compartments(id)
);

-- Tabla de alertas
CREATE TABLE alerts (
    id BIGSERIAL PRIMARY KEY,
    locker_id BIGINT NOT NULL,
    compartment_id BIGINT,
    alert_type VARCHAR(30) NOT NULL,
    severity VARCHAR(20) NOT NULL,
    message VARCHAR(500) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    resolved_at TIMESTAMP,
    FOREIGN KEY (locker_id) REFERENCES lockers(id),
    FOREIGN KEY (compartment_id) REFERENCES compartments(id)
);

-- Índices para mejorar rendimiento
CREATE INDEX idx_compartments_locker_id ON compartments(locker_id);
CREATE INDEX idx_compartments_status ON compartments(status);
CREATE INDEX idx_packages_tracking_number ON packages(tracking_number);
CREATE INDEX idx_packages_status ON packages(status);
CREATE INDEX idx_couriers_employee_id ON couriers(employee_id);
CREATE INDEX idx_deposits_package_id ON deposits(package_id);
CREATE INDEX idx_retrieval_codes_code ON retrieval_codes(code);
CREATE INDEX idx_retrieval_codes_deposit_id ON retrieval_codes(deposit_id);
CREATE INDEX idx_status_history_compartment_id ON status_history(compartment_id);
CREATE INDEX idx_alerts_locker_id ON alerts(locker_id);
CREATE INDEX idx_alerts_resolved_at ON alerts(resolved_at);
