#!/bin/bash
# generate_and_update.sh - Generar hashes BCrypt y actualizar migración

RAILWAY_URL="https://casilleroback-production.up.railway.app"

echo "🔐 Generando Hashes BCrypt desde Railway"
echo "=========================================="
echo ""
echo "Esperando 30 segundos para que Railway termine el deploy..."
sleep 30

echo "Obteniendo hashes..."
response=$(curl -s "$RAILWAY_URL/api/dev/hash-all")

echo "$response" | python3 -m json.tool

echo ""
echo "=========================================="
echo ""
echo "📋 Copia los hashes y actualiza V7__add_more_couriers.sql"
echo ""
echo "Ejemplo:"
echo "UPDATE couriers SET pin = 'HASH_DE_5678' WHERE employee_id = 'COUR002';"
echo ""
echo "Luego ejecuta:"
echo "  git add src/main/resources/db/migration/V7__add_more_couriers.sql"
echo "  git commit -m 'fix: PINs únicos para cada usuario'"
echo "  git push origin main"
