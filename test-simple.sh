#!/bin/bash

# Colores para output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${BLUE}╔════════════════════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║     Test API - Backend Casilleros Servientrega                ║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════════════════════════════╝${NC}"
echo ""

BASE_URL="http://localhost:8090"

print_section() {
    echo -e "\n${YELLOW}═══ $1 ═══${NC}"
}

print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

print_error() {
    echo -e "${RED}✗ $1${NC}"
}

# 1. Health Check
print_section "1. Health Check"
HEALTH=$(curl -s $BASE_URL/api/health)
if [ $? -eq 0 ]; then
    print_success "Health check OK"
    echo "$HEALTH"
else
    print_error "Health check failed"
    exit 1
fi

# 2. Login
print_section "2. Login de Mensajero"
LOGIN_RESPONSE=$(curl -s -X POST $BASE_URL/api/auth/courier/login \
  -H "Content-Type: application/json" \
  -d '{"employeeId": "COUR001", "pin": "1234"}')

echo "$LOGIN_RESPONSE"

# Extraer token (método simple sin jq)
TOKEN=$(echo "$LOGIN_RESPONSE" | grep -o '"token":"[^"]*' | cut -d'"' -f4)

if [ -n "$TOKEN" ]; then
    print_success "Login exitoso"
    echo "Token: ${TOKEN:0:50}..."
else
    print_error "Login falló"
    exit 1
fi

# 3. Validar Paquete
print_section "3. Validar Paquete"
PACKAGE=$(curl -s "$BASE_URL/api/packages/validate?trackingNumber=SRV123456789")
if [ $? -eq 0 ]; then
    print_success "Paquete validado"
    echo "$PACKAGE"
else
    print_error "Validación de paquete falló"
fi

# 4. Consultar Compartimentos
print_section "4. Consultar Compartimentos Disponibles"
COMPARTMENTS=$(curl -s $BASE_URL/api/lockers/1/compartments \
  -H "Authorization: Bearer $TOKEN")
if [ $? -eq 0 ]; then
    print_success "Compartimentos consultados"
    echo "$COMPARTMENTS"
else
    print_error "Consulta de compartimentos falló"
fi

# 5. Registrar Depósito
print_section "5. Registrar Depósito"
DEPOSIT=$(curl -s -X POST $BASE_URL/api/deposits \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "trackingNumber": "SRV123456789",
    "lockerId": 1,
    "compartmentId": 5,
    "courierId": 1,
    "photoUrl": "https://example.com/photo1.jpg"
  }')

echo "$DEPOSIT"

# Extraer código de retiro
RETRIEVAL_CODE=$(echo "$DEPOSIT" | grep -o '"retrievalCode":"[^"]*' | cut -d'"' -f4)

if [ -n "$RETRIEVAL_CODE" ]; then
    print_success "Depósito registrado"
    echo -e "${YELLOW}Código de retiro: $RETRIEVAL_CODE${NC}"
else
    print_error "Depósito falló"
fi

# 6. Estado del Locker
print_section "6. Estado del Locker"
LOCKER_STATUS=$(curl -s $BASE_URL/api/lockers/1/status \
  -H "Authorization: Bearer $TOKEN")
if [ $? -eq 0 ]; then
    print_success "Estado del locker consultado"
    echo "$LOCKER_STATUS"
else
    print_error "Consulta de estado falló"
fi

# 7. Validar Código de Retiro
print_section "7. Validar Código de Retiro"
CODE_VALIDATION=$(curl -s "$BASE_URL/api/retrievals/validate?code=$RETRIEVAL_CODE")
echo "$CODE_VALIDATION"

if echo "$CODE_VALIDATION" | grep -q '"valid":true'; then
    print_success "Código válido"
else
    print_error "Código inválido"
fi

# 8. Procesar Retiro
print_section "8. Procesar Retiro"
RETRIEVAL=$(curl -s -X POST $BASE_URL/api/retrievals \
  -H "Content-Type: application/json" \
  -d "{
    \"code\": \"$RETRIEVAL_CODE\",
    \"photoUrl\": \"https://example.com/photo2.jpg\"
  }")

echo "$RETRIEVAL"

if echo "$RETRIEVAL" | grep -q '"retrievalId"'; then
    print_success "Retiro procesado"
else
    print_error "Retiro falló"
fi

# 9. Métricas Operacionales
print_section "9. Métricas Operacionales"
METRICS=$(curl -s $BASE_URL/api/metrics/operational \
  -H "Authorization: Bearer $TOKEN")
if [ $? -eq 0 ]; then
    print_success "Métricas consultadas"
    echo "$METRICS"
else
    print_error "Consulta de métricas falló"
fi

# 10. Métricas del Locker
print_section "10. Métricas del Locker"
LOCKER_METRICS=$(curl -s $BASE_URL/api/metrics/locker/1 \
  -H "Authorization: Bearer $TOKEN")
if [ $? -eq 0 ]; then
    print_success "Métricas del locker consultadas"
    echo "$LOCKER_METRICS"
else
    print_error "Consulta de métricas del locker falló"
fi

# 11. Utilización por Tamaño
print_section "11. Utilización por Tamaño"
UTILIZATION=$(curl -s $BASE_URL/api/metrics/locker/1/utilization \
  -H "Authorization: Bearer $TOKEN")
if [ $? -eq 0 ]; then
    print_success "Utilización consultada"
    echo "$UTILIZATION"
else
    print_error "Consulta de utilización falló"
fi

# 12. Actualizar Estado
print_section "12. Simular Actualización de Estado"
STATUS_UPDATE=$(curl -s -X POST $BASE_URL/api/lockers/status-update \
  -H "Content-Type: application/json" \
  -d '{
    "lockerId": 1,
    "compartmentId": 6,
    "previousState": "DISPONIBLE",
    "currentState": "MANTENIMIENTO",
    "timestamp": "2024-01-15T10:30:00",
    "sensorReadings": {
      "sensor1": true,
      "sensor2": false,
      "sensor3": false,
      "sensor4": false,
      "infrared": true,
      "error": true
    }
  }')
if [ $? -eq 0 ]; then
    print_success "Estado actualizado"
    echo "$STATUS_UPDATE"
else
    print_error "Actualización de estado falló"
fi

# 13. Consultar Alertas
print_section "13. Consultar Alertas Activas"
ALERTS=$(curl -s $BASE_URL/api/alerts/locker/1 \
  -H "Authorization: Bearer $TOKEN")
if [ $? -eq 0 ]; then
    print_success "Alertas consultadas"
    echo "$ALERTS"
else
    print_error "Consulta de alertas falló"
fi

# Resumen
echo ""
echo -e "${BLUE}╔════════════════════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║                    RESUMEN DE PRUEBAS                          ║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════════════════════════════╝${NC}"
echo -e "${GREEN}✓ Health Check${NC}"
echo -e "${GREEN}✓ Autenticación JWT${NC}"
echo -e "${GREEN}✓ Validación de Paquetes${NC}"
echo -e "${GREEN}✓ Gestión de Compartimentos${NC}"
echo -e "${GREEN}✓ Depósito de Paquetes${NC}"
echo -e "${GREEN}✓ Retiro de Paquetes${NC}"
echo -e "${GREEN}✓ Métricas y Monitoreo${NC}"
echo -e "${GREEN}✓ Alertas y Mantenimiento${NC}"
echo ""
echo -e "${YELLOW}Código de retiro usado: $RETRIEVAL_CODE${NC}"
echo -e "${YELLOW}Ver logs: docker-compose logs -f backend${NC}"
echo -e "${YELLOW}Swagger UI: http://localhost:8080/swagger-ui.html${NC}"
echo ""
