#!/bin/bash
# Script para instalar psql y ejecutar migraciones en Railway

# 1. Instalar PostgreSQL client
sudo apt-get update
sudo apt-get install -y postgresql-client

# 2. Conectar y ejecutar migraciones
export PGPASSWORD='bPQIynICuVrImafrXRZLcFWhsfRhKokO'

# 3. Verificar tablas existentes
psql -h gondola.proxy.rlwy.net -p 53909 -U postgres -d railway -c "\dt"

# 4. Ejecutar script de migraciones
psql -h gondola.proxy.rlwy.net -p 53909 -U postgres -d railway -f fix_missing_migrations.sql

# 5. Verificar que se creó la tabla admins
psql -h gondola.proxy.rlwy.net -p 53909 -U postgres -d railway -c "SELECT * FROM admins;"

# 6. Verificar que se creó la tabla recipients
psql -h gondola.proxy.rlwy.net -p 53909 -U postgres -d railway -c "SELECT * FROM recipients;"

echo ""
echo "✅ Migraciones ejecutadas. Ahora prueba el login:"
echo ""
echo "curl -X POST https://casilleroback-production.up.railway.app/api/auth/admin/login \\"
echo "  -H 'Content-Type: application/json' \\"
echo "  -d '{\"email\":\"admin@servientrega.com\",\"password\":\"Admin123!\"}'"
