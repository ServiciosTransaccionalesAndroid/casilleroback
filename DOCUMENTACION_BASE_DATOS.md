# 📊 Documentación de Base de Datos - Servientrega Locker

## Tabla de Contenidos
1. [Acceso a la Base de Datos](#acceso-a-la-base-de-datos)
2. [Diagrama de Entidades](#diagrama-de-entidades)
3. [Tablas y Campos](#tablas-y-campos)
4. [Consultas Útiles](#consultas-útiles)
5. [Relaciones](#relaciones)

---

## Acceso a la Base de Datos

### Opción 1: Adminer (Interfaz Web)

**URL:** http://localhost:8081

**Credenciales:**
```
Sistema: PostgreSQL
Servidor: postgres
Usuario: locker_user
Contraseña: locker_pass
Base de datos: locker_db
```

### Opción 2: Línea de Comandos

```bash
# Conectar a PostgreSQL
docker exec -it locker-postgres psql -U locker_user -d locker_db

# Comandos útiles dentro de psql:
\dt              # Listar todas las tablas
\d nombre_tabla  # Ver estructura de una tabla
\q               # Salir
```

### Opción 3: DBeaver / pgAdmin

**Configuración:**
```
Host: localhost
Puerto: 5432
Database: locker_db
Usuario: locker_user
Contraseña: locker_pass
```

---

## Diagrama de Entidades

```
┌─────────────┐
│   LOCKERS   │
└──────┬──────┘
       │ 1:N
       │
┌──────▼──────────┐
│  COMPARTMENTS   │
└──────┬──────────┘
       │ 1:N
       │
┌──────▼──────┐         ┌──────────────┐
│  DEPOSITS   │◄────────┤   PACKAGES   │
└──────┬──────┘   N:1   └──────────────┘
       │ 1:1
       │
┌──────▼─────────────┐
│  RETRIEVAL_CODES   │
└──────┬─────────────┘
       │ 1:N
       │
┌──────▼──────────┐
│   RETRIEVALS    │
└─────────────────┘

┌──────────────┐
│   COURIERS   │
└──────┬───────┘
       │ 1:N
       │
       └──────► DEPOSITS

┌──────────────┐
│    ALERTS    │
└──────────────┘

┌─────────────────┐
│ STATUS_HISTORY  │
└─────────────────┘
```

---

## Tablas y Campos

### 1. **lockers** - Casilleros Físicos

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | BIGSERIAL | ID único del locker |
| name | VARCHAR(100) | Nombre del locker (ej: "Locker Centro") |
| location | VARCHAR(255) | Ubicación general |
| address | VARCHAR(255) | Dirección completa |
| latitude | DECIMAL(10,8) | Coordenada GPS |
| longitude | DECIMAL(11,8) | Coordenada GPS |
| status | VARCHAR(20) | ACTIVO, INACTIVO, MANTENIMIENTO |
| created_at | TIMESTAMP | Fecha de creación |
| updated_at | TIMESTAMP | Última actualización |

**Consultar:**
```sql
SELECT * FROM lockers;
```

---

### 2. **compartments** - Compartimentos del Locker

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | BIGSERIAL | ID único del compartimento |
| locker_id | BIGINT | FK a lockers |
| compartment_number | INTEGER | Número del compartimento (1-12) |
| size | VARCHAR(20) | SMALL, MEDIUM, LARGE |
| width | DECIMAL(5,2) | Ancho en cm |
| height | DECIMAL(5,2) | Alto en cm |
| depth | DECIMAL(5,2) | Profundidad en cm |
| status | VARCHAR(20) | DISPONIBLE, OCUPADO, FUERA_SERVICIO |
| created_at | TIMESTAMP | Fecha de creación |
| updated_at | TIMESTAMP | Última actualización |

**Consultar compartimentos disponibles:**
```sql
SELECT 
    c.id,
    c.compartment_number,
    c.size,
    c.status,
    l.name as locker_name
FROM compartments c
JOIN lockers l ON c.locker_id = l.id
WHERE c.status = 'DISPONIBLE'
ORDER BY c.compartment_number;
```

---

### 3. **packages** - Paquetes

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | BIGSERIAL | ID único del paquete |
| tracking_number | VARCHAR(50) | Número de guía (ej: SRV123456789) |
| recipient_name | VARCHAR(100) | Nombre del destinatario |
| recipient_phone | VARCHAR(20) | Teléfono del destinatario |
| recipient_email | VARCHAR(100) | Email del destinatario |
| weight | DECIMAL(5,2) | Peso en kg |
| width | DECIMAL(5,2) | Ancho en cm |
| height | DECIMAL(5,2) | Alto en cm |
| depth | DECIMAL(5,2) | Profundidad en cm |
| status | VARCHAR(20) | EN_TRANSITO, EN_LOCKER, ENTREGADO |
| created_at | TIMESTAMP | Fecha de creación |
| updated_at | TIMESTAMP | Última actualización |

**Consultar paquetes:**
```sql
SELECT 
    tracking_number,
    recipient_name,
    recipient_email,
    status,
    created_at
FROM packages
ORDER BY created_at DESC;
```

---

### 4. **couriers** - Mensajeros

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | BIGSERIAL | ID único del mensajero |
| employee_id | VARCHAR(50) | Código de empleado (ej: COUR001) |
| name | VARCHAR(100) | Nombre completo |
| phone | VARCHAR(20) | Teléfono |
| email | VARCHAR(100) | Email |
| pin_hash | VARCHAR(255) | PIN encriptado (BCrypt) |
| status | VARCHAR(20) | ACTIVO, INACTIVO |
| created_at | TIMESTAMP | Fecha de creación |
| updated_at | TIMESTAMP | Última actualización |

**Consultar mensajeros:**
```sql
SELECT 
    employee_id,
    name,
    phone,
    status
FROM couriers
WHERE status = 'ACTIVO';
```

---

### 5. **deposits** - Depósitos de Paquetes

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | BIGSERIAL | ID único del depósito |
| package_id | BIGINT | FK a packages |
| compartment_id | BIGINT | FK a compartments |
| courier_id | BIGINT | FK a couriers |
| deposit_timestamp | TIMESTAMP | Fecha/hora del depósito |
| photo_url | VARCHAR(255) | URL de la foto del depósito |
| created_at | TIMESTAMP | Fecha de creación |
| updated_at | TIMESTAMP | Última actualización |

**Consultar depósitos recientes:**
```sql
SELECT 
    d.id,
    p.tracking_number,
    p.recipient_name,
    c.compartment_number,
    co.name as courier_name,
    d.deposit_timestamp
FROM deposits d
JOIN packages p ON d.package_id = p.id
JOIN compartments c ON d.compartment_id = c.id
JOIN couriers co ON d.courier_id = co.id
ORDER BY d.deposit_timestamp DESC
LIMIT 10;
```

---

### 6. **retrieval_codes** - Códigos de Retiro

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | BIGSERIAL | ID único del código |
| deposit_id | BIGINT | FK a deposits |
| code | VARCHAR(8) | Código alfanumérico (ej: A3K7M9P2) |
| secret_pin | VARCHAR(6) | PIN secreto de 6 dígitos |
| generated_at | TIMESTAMP | Fecha de generación |
| expires_at | TIMESTAMP | Fecha de expiración (48h) |
| used | BOOLEAN | Si ya fue usado |
| used_at | TIMESTAMP | Fecha de uso |
| created_at | TIMESTAMP | Fecha de creación |
| updated_at | TIMESTAMP | Última actualización |

**Consultar códigos activos:**
```sql
SELECT 
    rc.code,
    rc.secret_pin,
    p.tracking_number,
    p.recipient_name,
    rc.expires_at,
    rc.used,
    c.compartment_number
FROM retrieval_codes rc
JOIN deposits d ON rc.deposit_id = d.id
JOIN packages p ON d.package_id = p.id
JOIN compartments c ON d.compartment_id = c.id
WHERE rc.used = false 
  AND rc.expires_at > NOW()
ORDER BY rc.generated_at DESC;
```

---

### 7. **retrievals** - Retiros de Paquetes

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | BIGSERIAL | ID único del retiro |
| retrieval_code_id | BIGINT | FK a retrieval_codes |
| retrieval_timestamp | TIMESTAMP | Fecha/hora del retiro |
| created_at | TIMESTAMP | Fecha de creación |
| updated_at | TIMESTAMP | Última actualización |

**Consultar retiros:**
```sql
SELECT 
    r.id,
    rc.code,
    p.tracking_number,
    p.recipient_name,
    r.retrieval_timestamp
FROM retrievals r
JOIN retrieval_codes rc ON r.retrieval_code_id = rc.id
JOIN deposits d ON rc.deposit_id = d.id
JOIN packages p ON d.package_id = p.id
ORDER BY r.retrieval_timestamp DESC;
```

---

### 8. **alerts** - Alertas del Sistema

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | BIGSERIAL | ID único de la alerta |
| locker_id | BIGINT | FK a lockers |
| compartment_id | BIGINT | FK a compartments (opcional) |
| alert_type | VARCHAR(50) | COMPARTMENT_STUCK, DOOR_OPEN, etc. |
| severity | VARCHAR(20) | LOW, MEDIUM, HIGH, CRITICAL |
| message | TEXT | Descripción de la alerta |
| resolved | BOOLEAN | Si fue resuelta |
| resolved_at | TIMESTAMP | Fecha de resolución |
| created_at | TIMESTAMP | Fecha de creación |
| updated_at | TIMESTAMP | Última actualización |

**Consultar alertas activas:**
```sql
SELECT 
    a.id,
    l.name as locker_name,
    a.alert_type,
    a.severity,
    a.message,
    a.created_at
FROM alerts a
JOIN lockers l ON a.locker_id = l.id
WHERE a.resolved = false
ORDER BY a.severity DESC, a.created_at DESC;
```

---

### 9. **status_history** - Historial de Estados

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | BIGSERIAL | ID único del registro |
| entity_type | VARCHAR(50) | PACKAGE, COMPARTMENT, LOCKER |
| entity_id | BIGINT | ID de la entidad |
| old_status | VARCHAR(50) | Estado anterior |
| new_status | VARCHAR(50) | Estado nuevo |
| changed_by | VARCHAR(100) | Usuario que hizo el cambio |
| reason | TEXT | Razón del cambio |
| created_at | TIMESTAMP | Fecha del cambio |

**Consultar historial:**
```sql
SELECT 
    entity_type,
    entity_id,
    old_status,
    new_status,
    changed_by,
    created_at
FROM status_history
ORDER BY created_at DESC
LIMIT 20;
```

---

## Consultas Útiles

### Ver flujo completo de un paquete

```sql
SELECT 
    p.tracking_number,
    p.recipient_name,
    p.status as package_status,
    d.deposit_timestamp,
    rc.code as retrieval_code,
    rc.secret_pin,
    rc.expires_at,
    rc.used,
    r.retrieval_timestamp,
    c.compartment_number,
    l.name as locker_name
FROM packages p
LEFT JOIN deposits d ON p.id = d.package_id
LEFT JOIN retrieval_codes rc ON d.id = rc.deposit_id
LEFT JOIN retrievals r ON rc.id = r.retrieval_code_id
LEFT JOIN compartments c ON d.compartment_id = c.id
LEFT JOIN lockers l ON c.locker_id = l.id
WHERE p.tracking_number = 'SRV111222333';
```

### Estadísticas del día

```sql
-- Depósitos del día
SELECT COUNT(*) as depositos_hoy
FROM deposits
WHERE DATE(deposit_timestamp) = CURRENT_DATE;

-- Retiros del día
SELECT COUNT(*) as retiros_hoy
FROM retrievals
WHERE DATE(retrieval_timestamp) = CURRENT_DATE;

-- Compartimentos ocupados
SELECT COUNT(*) as compartimentos_ocupados
FROM compartments
WHERE status = 'OCUPADO';

-- Códigos expirados sin usar
SELECT COUNT(*) as codigos_expirados
FROM retrieval_codes
WHERE used = false 
  AND expires_at < NOW();
```

### Buscar paquete por código de retiro

```sql
SELECT 
    p.tracking_number,
    p.recipient_name,
    p.recipient_phone,
    p.recipient_email,
    rc.code,
    rc.secret_pin,
    rc.expires_at,
    c.compartment_number,
    l.name as locker_name,
    l.address
FROM retrieval_codes rc
JOIN deposits d ON rc.deposit_id = d.id
JOIN packages p ON d.package_id = p.id
JOIN compartments c ON d.compartment_id = c.id
JOIN lockers l ON c.locker_id = l.id
WHERE rc.code = 'KM2KSX6Y';
```

### Ver todos los paquetes de un cliente

```sql
SELECT 
    p.tracking_number,
    p.status,
    d.deposit_timestamp,
    rc.code,
    rc.used,
    r.retrieval_timestamp
FROM packages p
LEFT JOIN deposits d ON p.id = d.package_id
LEFT JOIN retrieval_codes rc ON d.id = rc.deposit_id
LEFT JOIN retrievals r ON rc.id = r.retrieval_code_id
WHERE p.recipient_email = 'systemscenter@hotmail.com'
ORDER BY p.created_at DESC;
```

### Compartimentos disponibles por tamaño

```sql
SELECT 
    size,
    COUNT(*) as cantidad_disponible
FROM compartments
WHERE status = 'DISPONIBLE'
GROUP BY size
ORDER BY 
    CASE size
        WHEN 'SMALL' THEN 1
        WHEN 'MEDIUM' THEN 2
        WHEN 'LARGE' THEN 3
    END;
```

---

## Relaciones entre Tablas

### 1. Locker → Compartments (1:N)
Un locker tiene múltiples compartimentos.

### 2. Compartment → Deposits (1:N)
Un compartimento puede tener múltiples depósitos (histórico).

### 3. Package → Deposits (1:N)
Un paquete puede ser depositado múltiples veces (reenvíos).

### 4. Courier → Deposits (1:N)
Un mensajero realiza múltiples depósitos.

### 5. Deposit → RetrievalCode (1:1)
Cada depósito genera un único código de retiro.

### 6. RetrievalCode → Retrievals (1:N)
Un código puede tener múltiples intentos de retiro (histórico).

---

## Scripts de Mantenimiento

### Limpiar códigos expirados

```sql
-- Ver códigos expirados
SELECT 
    rc.code,
    p.tracking_number,
    rc.expires_at
FROM retrieval_codes rc
JOIN deposits d ON rc.deposit_id = d.id
JOIN packages p ON d.package_id = p.id
WHERE rc.used = false 
  AND rc.expires_at < NOW();

-- Marcar paquetes como no entregados
UPDATE packages
SET status = 'NO_ENTREGADO'
WHERE id IN (
    SELECT p.id
    FROM packages p
    JOIN deposits d ON p.id = d.package_id
    JOIN retrieval_codes rc ON d.id = rc.deposit_id
    WHERE rc.used = false 
      AND rc.expires_at < NOW()
);
```

### Backup de la base de datos

```bash
# Crear backup
docker exec locker-postgres pg_dump -U locker_user locker_db > backup_$(date +%Y%m%d).sql

# Restaurar backup
cat backup_20260219.sql | docker exec -i locker-postgres psql -U locker_user -d locker_db
```

---

## Índices Importantes

La base de datos tiene los siguientes índices para optimizar consultas:

```sql
-- Índices en packages
CREATE INDEX idx_packages_tracking ON packages(tracking_number);
CREATE INDEX idx_packages_status ON packages(status);

-- Índices en retrieval_codes
CREATE INDEX idx_retrieval_codes_code ON retrieval_codes(code);
CREATE INDEX idx_retrieval_codes_used ON retrieval_codes(used);
CREATE INDEX idx_retrieval_codes_expires ON retrieval_codes(expires_at);

-- Índices en compartments
CREATE INDEX idx_compartments_status ON compartments(status);
CREATE INDEX idx_compartments_locker ON compartments(locker_id);

-- Índices en deposits
CREATE INDEX idx_deposits_package ON deposits(package_id);
CREATE INDEX idx_deposits_timestamp ON deposits(deposit_timestamp);
```

---

## Contacto y Soporte

Para más información sobre la base de datos, consulta:
- **README.md** - Información general del proyecto
- **PLAN_IMPLEMENTACION.md** - Plan de desarrollo
- **GUIA_IMPLEMENTACION_API.md** - Documentación de la API

---

**Última actualización:** 2026-02-19
