# Backup de Base de Datos - Casilleros Servientrega

Backup creado: $(date)

## Archivos incluidos

- `admins.csv` - Usuarios administradores
- `alerts.csv` - Alertas del sistema
- `compartments.csv` - Casilleros/compartimentos
- `couriers.csv` - Mensajeros
- `deposits.csv` - Depósitos realizados
- `lockers.csv` - Lockers físicos
- `operation_logs.csv` - Logs de operaciones
- `packages.csv` - Paquetes
- `recipients.csv` - Destinatarios
- `retrieval_codes.csv` - Códigos de retiro
- `retrievals.csv` - Retiros realizados
- `status_history.csv` - Historial de estados

## Restaurar en otra base de datos

### Opción 1: Usando el script (Recomendado)

```bash
cd backup
./restore.sh <host> <port> <database> <username>
```

**Ejemplo local:**
```bash
./restore.sh localhost 5432 locker_db locker_user
```

**Ejemplo Railway:**
```bash
./restore.sh gondola.proxy.rlwy.net 53909 railway postgres
```

### Opción 2: Manual con psql

```bash
export PGPASSWORD='tu_password'

psql -h localhost -p 5432 -U locker_user -d locker_db -c "\copy admins FROM 'admins.csv' CSV HEADER"
psql -h localhost -p 5432 -U locker_user -d locker_db -c "\copy lockers FROM 'lockers.csv' CSV HEADER"
psql -h localhost -p 5432 -U locker_user -d locker_db -c "\copy compartments FROM 'compartments.csv' CSV HEADER"
psql -h localhost -p 5432 -U locker_user -d locker_db -c "\copy recipients FROM 'recipients.csv' CSV HEADER"
psql -h localhost -p 5432 -U locker_user -d locker_db -c "\copy packages FROM 'packages.csv' CSV HEADER"
psql -h localhost -p 5432 -U locker_user -d locker_db -c "\copy couriers FROM 'couriers.csv' CSV HEADER"
psql -h localhost -p 5432 -U locker_user -d locker_db -c "\copy deposits FROM 'deposits.csv' CSV HEADER"
psql -h localhost -p 5432 -U locker_user -d locker_db -c "\copy retrieval_codes FROM 'retrieval_codes.csv' CSV HEADER"
psql -h localhost -p 5432 -U locker_user -d locker_db -c "\copy retrievals FROM 'retrievals.csv' CSV HEADER"
psql -h localhost -p 5432 -U locker_user -d locker_db -c "\copy alerts FROM 'alerts.csv' CSV HEADER"
psql -h localhost -p 5432 -U locker_user -d locker_db -c "\copy operation_logs FROM 'operation_logs.csv' CSV HEADER"
psql -h localhost -p 5432 -U locker_user -d locker_db -c "\copy status_history FROM 'status_history.csv' CSV HEADER"
```

## Notas importantes

1. **La base de datos destino debe existir** y tener las tablas creadas (ejecutar migraciones Flyway primero)
2. **Las tablas deben estar vacías** o usar `TRUNCATE` antes de restaurar
3. **Respetar el orden** de restauración por dependencias de claves foráneas
4. El script actualiza automáticamente las secuencias de IDs

## Limpiar base de datos antes de restaurar

```sql
TRUNCATE TABLE status_history, operation_logs, alerts, retrievals, retrieval_codes, 
                deposits, couriers, packages, recipients, compartments, lockers, admins 
CASCADE;
```
