#!/bin/bash

echo "╔════════════════════════════════════════════════════════════════╗"
echo "║     DEPOSITAR PAQUETE Y OBTENER CÓDIGO DE RETIRO              ║"
echo "╚════════════════════════════════════════════════════════════════╝"
echo ""

# Paquete a depositar
TRACKING="SRV123456789"

echo "📦 Paquete: $TRACKING"
echo ""

# Paso 1: Validar que el paquete existe
echo "1️⃣  Validando paquete..."
PACKAGE=$(curl -s "http://localhost:8090/api/packages/validate?trackingNumber=$TRACKING")
echo "$PACKAGE"
echo ""

# Paso 2: Hacer depósito usando Swagger UI
echo "2️⃣  Para depositar el paquete y obtener el código:"
echo ""
echo "   Opción A - Swagger UI (MÁS FÁCIL):"
echo "   ======================================"
echo "   1. Abre: http://localhost:8090/swagger-ui.html"
echo "   2. Ve a 'deposit-controller' → POST /api/deposits"
echo "   3. Click 'Try it out'"
echo "   4. Usa estos datos:"
echo ""
echo "   {"
echo "     \"trackingNumber\": \"$TRACKING\","
echo "     \"lockerId\": 1,"
echo "     \"compartmentId\": 5,"
echo "     \"courierId\": 1"
echo "   }"
echo ""
echo "   5. Click 'Execute'"
echo "   6. Verás el código en la respuesta"
echo ""
echo "   Opción B - Consultar en Base de Datos:"
echo "   ======================================="
echo "   docker exec locker-postgres psql -U locker_user -d locker_db -c \\"
echo "   \"SELECT rc.code FROM retrieval_codes rc"
echo "   JOIN deposits d ON rc.deposit_id = d.id"
echo "   JOIN packages p ON d.package_id = p.id"
echo "   WHERE p.tracking_number = '$TRACKING';\""
echo ""
echo "╔════════════════════════════════════════════════════════════════╗"
echo "║  NOTA: El código se genera AUTOMÁTICAMENTE al hacer depósito  ║"
echo "╚════════════════════════════════════════════════════════════════╝"
