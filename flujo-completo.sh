#!/bin/bash

echo "╔════════════════════════════════════════════════════════════════╗"
echo "║     FLUJO COMPLETO DE TRACKING - SRV999888777                  ║"
echo "╚════════════════════════════════════════════════════════════════╝"
echo ""

TRACKING="SRV999888777"

# PASO 1: Validar paquete
echo "📦 PASO 1: Validar paquete"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
curl -s "http://localhost:8090/api/packages/validate?trackingNumber=$TRACKING" | python3 -m json.tool
echo ""
echo ""

# PASO 2: Login mensajero
echo "🔐 PASO 2: Login mensajero"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
LOGIN=$(curl -s -X POST http://localhost:8090/api/auth/courier/login \
  -H "Content-Type: application/json" \
  -d '{"employeeId": "COUR001", "pin": "1234"}')
echo "$LOGIN" | python3 -m json.tool
TOKEN=$(echo $LOGIN | grep -o '"token":"[^"]*' | cut -d'"' -f4)
echo ""
echo "✅ Token obtenido"
echo ""

# PASO 3: Ver compartimentos disponibles
echo "🚪 PASO 3: Ver compartimentos disponibles"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
curl -s "http://localhost:8090/api/lockers/1/compartments" \
  -H "Authorization: Bearer $TOKEN" | python3 -m json.tool
echo ""
echo ""

# PASO 4: Depositar paquete (usar compartimento 7)
echo "📥 PASO 4: Depositar paquete en compartimento 7"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
DEPOSIT=$(curl -s -X POST http://localhost:8090/api/deposits \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "{
    \"trackingNumber\": \"$TRACKING\",
    \"lockerId\": 1,
    \"compartmentId\": 7,
    \"courierId\": 1
  }")
echo "$DEPOSIT" | python3 -m json.tool
CODE=$(echo $DEPOSIT | grep -o '"retrievalCode":"[^"]*' | cut -d'"' -f4)
echo ""
echo "✅ Código de retiro generado: $CODE"
echo ""

# PASO 5: Generar QR
echo "📱 PASO 5: Generar código QR"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
QR_FILE="/tmp/qr_${CODE}.png"
curl -s "http://localhost:8090/api/qr/retrieval-code/$CODE" -o "$QR_FILE"
if [ -f "$QR_FILE" ]; then
  echo "✅ QR generado: $QR_FILE"
  echo "🌐 Ver en navegador: http://localhost:8090/api/qr/retrieval-code/$CODE"
else
  echo "❌ Error al generar QR"
fi
echo ""
echo ""

# PASO 6: Validar código de retiro (como cliente)
echo "🔍 PASO 6: Validar código de retiro (Cliente)"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
curl -s "http://localhost:8090/api/retrievals/validate?code=$CODE" | python3 -m json.tool
echo ""
echo ""

# PASO 7: Procesar retiro (como cliente)
echo "📤 PASO 7: Procesar retiro (Cliente)"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
RETRIEVAL=$(curl -s -X POST http://localhost:8090/api/retrievals \
  -H "Content-Type: application/json" \
  -d "{\"code\": \"$CODE\"}")
echo "$RETRIEVAL" | python3 -m json.tool
echo ""
echo ""

# Resumen final
echo "╔════════════════════════════════════════════════════════════════╗"
echo "║                    RESUMEN DEL FLUJO                           ║"
echo "╠════════════════════════════════════════════════════════════════╣"
echo "║  Tracking Number: $TRACKING                              ║"
echo "║  Código de Retiro: $CODE                                  ║"
echo "║  QR: $QR_FILE                         ║"
echo "║  Estado: COMPLETADO ✅                                         ║"
echo "╚════════════════════════════════════════════════════════════════╝"
