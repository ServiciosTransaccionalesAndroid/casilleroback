# 🔧 Fix: Migraciones Faltantes en Railway

## Problema
Las migraciones V9 (admins) y V10 (recipients) no se ejecutaron en Railway, causando que el login de admin falle.

## Solución

### Opción 1: Ejecutar SQL Manualmente en Railway

1. **Ir a Railway Dashboard:**
   ```
   https://railway.app
   ```

2. **Seleccionar el proyecto:** `casilleroback-production`

3. **Ir al servicio PostgreSQL** → **Data** → **Query**

4. **Copiar y ejecutar el contenido de:** `fix_missing_migrations.sql`

### Opción 2: Conectarse desde Terminal

```bash
# Obtener la URL de conexión desde Railway
# Variables → POSTGRES_URL

# Conectarse con psql
psql "postgresql://postgres:PASSWORD@HOST:PORT/railway"

# Ejecutar el script
\i fix_missing_migrations.sql

# Verificar tablas
\dt

# Verificar admin
SELECT * FROM admins;

# Verificar recipients
SELECT * FROM recipients;
```

### Opción 3: Forzar Re-deploy con Migraciones

```bash
# Limpiar Flyway y forzar re-ejecución
# En Railway, agregar variable de entorno temporal:
SPRING_FLYWAY_CLEAN_ON_VALIDATION_ERROR=true

# Hacer un cambio y push para forzar deploy
git commit --allow-empty -m "force redeploy"
git push origin main

# IMPORTANTE: Remover la variable después del deploy
```

## Verificar que Funcionó

```bash
# Test login admin
curl -X POST https://casilleroback-production.up.railway.app/api/auth/admin/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@servientrega.com","password":"Admin123!"}'

# Debe retornar:
# {
#   "token": "eyJhbGc...",
#   "email": "admin@servientrega.com",
#   "name": "Administrador Principal",
#   "role": "ADMIN",
#   "message": "Login successful"
# }
```

## Credenciales de Prueba

| Email | Password | Rol |
|-------|----------|-----|
| admin@servientrega.com | Admin123! | ADMIN |

## Tablas que se Crean

1. **admins** - Usuarios del portal administrativo
2. **recipients** - Clientes/destinatarios de paquetes

## Datos de Prueba Incluidos

### Admins
- 1 administrador principal

### Recipients
- Carlos Rodríguez (+573101234567)
- Ana Martínez (+573109876543)
- Pedro Sánchez (+573105556677)
