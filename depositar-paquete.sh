#!/bin/bash

echo "╔════════════════════════════════════════════════════════════════╗"
echo "║     DEPOSITAR PAQUETE SRV123456789 Y OBTENER CÓDIGO           ║"
echo "╚════════════════════════════════════════════════════════════════╝"
echo ""

# 1. Login como mensajero
echo "🔐 1. Autenticando mensajero..."
LOGIN_RESPONSE=$(curl -s -X POST "http://localhost:8090/api/auth/courier/login" \
  -H "Content-Type: application/json" \
  -d '{
    "courierCode": "MSG001",
    "pin": "1234"
  }')

TOKEN=$(echo $LOGIN_RESPONSE | grep -o '"token":"[^"]*' | cut -d'"' -f4)

if [ -z "$TOKEN" ]; then
  echo "❌ Error en login. Respuesta:"
  echo "$LOGIN_RESPONSE"
  echo ""
  echo "💡 Solución: Usa Swagger UI en http://localhost:8090/swagger-ui.html"
  exit 1
fi

echo "✅ Token obtenido"
echo ""

# 2. Depositar paquete
echo "📦 2. Depositando paquete SRV123456789..."
DEPOSIT_RESPONSE=$(curl -s -X POST "http://localhost:8090/api/deposits" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "trackingNumber": "SRV123456789",
    "lockerId": 1,
    "compartmentId": 5,
    "courierId": 1
  }')

echo "$DEPOSIT_RESPONSE" | python3 -m json.tool 2>/dev/null || echo "$DEPOSIT_RESPONSE"
echo ""

# 3. Extraer código de retiro
CODE=$(echo $DEPOSIT_RESPONSE | grep -o '"retrievalCode":"[^"]*' | cut -d'"' -f4)

if [ -n "$CODE" ]; then
  echo "╔════════════════════════════════════════════════════════════════╗"
  echo "║  ✅ CÓDIGO DE RETIRO GENERADO: $CODE                    ║"
  echo "╚════════════════════════════════════════════════════════════════╝"
  echo ""
  echo "📱 El cliente puede retirar con este código en el locker"
else
  echo "❌ No se pudo generar el código. Ver respuesta arriba."
fi
