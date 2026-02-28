#!/bin/bash

# Script para obtener código de retiro y generar QR
# Uso: ./consultar-codigo-qr.sh SRV123456789

TRACKING=$1

if [ -z "$TRACKING" ]; then
  echo "❌ Error: Debes proporcionar un tracking number"
  echo "Uso: $0 SRV123456789"
  exit 1
fi

echo "╔════════════════════════════════════════════════════════════════╗"
echo "║     CONSULTAR CÓDIGO DE RETIRO Y GENERAR QR                   ║"
echo "╚════════════════════════════════════════════════════════════════╝"
echo ""
echo "📦 Tracking Number: $TRACKING"
echo ""

# 1. Obtener código de retiro de la base de datos
echo "🔍 Consultando código de retiro..."
CODE=$(docker exec locker-postgres psql -U locker_user -d locker_db -t -c \
  "SELECT rc.code FROM retrieval_codes rc 
   JOIN deposits d ON rc.deposit_id = d.id 
   JOIN packages p ON d.package_id = p.id 
   WHERE p.tracking_number = '$TRACKING';" | xargs)

if [ -z "$CODE" ]; then
  echo "❌ No se encontró código de retiro para $TRACKING"
  echo ""
  echo "💡 Posibles razones:"
  echo "   1. El paquete no ha sido depositado aún"
  echo "   2. El tracking number es incorrecto"
  echo ""
  echo "Para depositar el paquete, usa Swagger UI:"
  echo "http://localhost:8090/swagger-ui.html"
  exit 1
fi

echo "✅ Código encontrado: $CODE"
echo ""

# 2. Obtener información completa del código
echo "📋 Información del código:"
docker exec locker-postgres psql -U locker_user -d locker_db -c \
  "SELECT 
     p.tracking_number AS guia,
     p.recipient_name AS destinatario,
     rc.code AS codigo_retiro,
     rc.expires_at AS expira,
     rc.used AS usado,
     c.compartment_number AS casillero,
     l.name AS locker
   FROM retrieval_codes rc
   JOIN deposits d ON rc.deposit_id = d.id
   JOIN packages p ON d.package_id = p.id
   JOIN compartments c ON d.compartment_id = c.id
   JOIN lockers l ON c.locker_id = l.id
   WHERE p.tracking_number = '$TRACKING';"
echo ""

# 3. Validar código con la API
echo "🔐 Validando código con la API..."
VALIDATION=$(curl -s "http://localhost:8090/api/retrievals/validate?code=$CODE")
echo "$VALIDATION" | python3 -m json.tool 2>/dev/null || echo "$VALIDATION"
echo ""

# 4. Generar QR
echo "📱 Generando código QR..."
QR_FILE="/tmp/qr_${CODE}.png"
curl -s "http://localhost:8090/api/qr/retrieval-code/$CODE" -o "$QR_FILE"

if [ -f "$QR_FILE" ]; then
  FILE_INFO=$(file "$QR_FILE")
  if [[ $FILE_INFO == *"PNG image"* ]]; then
    echo "✅ QR generado exitosamente: $QR_FILE"
    echo ""
    echo "╔════════════════════════════════════════════════════════════════╗"
    echo "║  CÓDIGO DE RETIRO: $CODE                              ║"
    echo "║  QR GUARDADO EN: $QR_FILE                    ║"
    echo "╚════════════════════════════════════════════════════════════════╝"
    echo ""
    echo "🌐 También puedes ver el QR en el navegador:"
    echo "http://localhost:8090/api/qr/retrieval-code/$CODE"
  else
    echo "❌ Error al generar QR"
  fi
else
  echo "❌ No se pudo crear el archivo QR"
fi
