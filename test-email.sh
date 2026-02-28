#!/bin/bash

TRACKING="SRV111222333"

echo "╔════════════════════════════════════════════════════════════════╗"
echo "║     DEPOSITAR PAQUETE Y ENVIAR CORREO                          ║"
echo "╚════════════════════════════════════════════════════════════════╝"
echo ""
echo "📦 Paquete: $TRACKING"
echo "📧 Email: systemscenter@hotmail.com"
echo ""

# Login
echo "🔐 Login mensajero..."
LOGIN=$(curl -s -X POST http://localhost:8090/api/auth/courier/login \
  -H "Content-Type: application/json" \
  -d '{"employeeId": "COUR001", "pin": "1234"}')
TOKEN=$(echo $LOGIN | grep -o '"token":"[^"]*' | cut -d'"' -f4)
echo "✅ Token obtenido"
echo ""

# Depositar
echo "📥 Depositando paquete..."
DEPOSIT=$(curl -s -X POST http://localhost:8090/api/deposits \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "{
    \"trackingNumber\": \"$TRACKING\",
    \"lockerId\": 1,
    \"compartmentId\": 1,
    \"courierId\": 1
  }")

echo "$DEPOSIT" | python3 -m json.tool
CODE=$(echo $DEPOSIT | grep -o '"retrievalCode":"[^"]*' | cut -d'"' -f4)
echo ""
echo "╔════════════════════════════════════════════════════════════════╗"
echo "║  ✅ DEPÓSITO COMPLETADO                                        ║"
echo "║  Código: $CODE                                          ║"
echo "║  📧 Correo enviado a: systemscenter@hotmail.com                ║"
echo "╚════════════════════════════════════════════════════════════════╝"
