#!/bin/bash
# test_pins.sh - Script para probar el sistema de autenticación por PIN

BASE_URL="http://localhost:8080"
API_URL="${BASE_URL}/api/auth/courier/login"

echo "🔐 Probando Sistema de Autenticación por PIN"
echo "=============================================="
echo ""

# Colores
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Función para hacer login
test_login() {
    local employee_id=$1
    local pin=$2
    local description=$3
    
    echo -e "${YELLOW}Probando:${NC} $description"
    echo "  Employee ID: $employee_id"
    echo "  PIN: $pin"
    
    response=$(curl -s -w "\n%{http_code}" -X POST "$API_URL" \
        -H "Content-Type: application/json" \
        -d "{\"employeeId\": \"$employee_id\", \"pin\": \"$pin\"}")
    
    http_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | head -n-1)
    
    if [ "$http_code" -eq 200 ]; then
        echo -e "  ${GREEN}✅ ÉXITO${NC} (HTTP $http_code)"
        echo "  Token: $(echo $body | grep -o '"token":"[^"]*' | cut -d'"' -f4 | cut -c1-20)..."
    else
        echo -e "  ${RED}❌ FALLÓ${NC} (HTTP $http_code)"
        echo "  Error: $body"
    fi
    echo ""
}

# Verificar que el servidor esté corriendo
echo "Verificando servidor..."
if ! curl -s "$BASE_URL/api/health" > /dev/null 2>&1; then
    echo -e "${RED}❌ Error: El servidor no está corriendo en $BASE_URL${NC}"
    echo "Ejecuta: docker-compose up -d"
    exit 1
fi
echo -e "${GREEN}✅ Servidor corriendo${NC}"
echo ""

# Casos de prueba exitosos
echo "=== CASOS EXITOSOS ==="
echo ""
test_login "COUR001" "1234" "Juan Pérez - PIN 4 dígitos"
test_login "COUR002" "5678" "María González - PIN 4 dígitos"
test_login "COUR003" "9012" "Luis Ramírez - PIN 4 dígitos"
test_login "COUR004" "123456" "Sandra López - PIN 6 dígitos"

# Casos de prueba fallidos
echo "=== CASOS DE ERROR ==="
echo ""
test_login "COUR001" "9999" "PIN incorrecto"
test_login "COUR001" "abc" "PIN con letras (formato inválido)"
test_login "COUR001" "123" "PIN muy corto (3 dígitos)"
test_login "COUR001" "1234567" "PIN muy largo (7 dígitos)"
test_login "COUR999" "1234" "Usuario no existe"

echo "=============================================="
echo "✅ Pruebas completadas"
echo ""
echo "Para ver más detalles, revisa Swagger UI:"
echo "  $BASE_URL/swagger-ui.html"
