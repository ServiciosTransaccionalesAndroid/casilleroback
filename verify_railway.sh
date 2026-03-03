#!/bin/bash
# verify_railway.sh - Verificar deployment en Railway

RAILWAY_URL="https://casilleroback-production.up.railway.app"

echo "🔍 Verificando Deployment en Railway"
echo "===================================="
echo ""

# Colores
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

# 1. Health Check
echo -e "${YELLOW}1. Health Check${NC}"
response=$(curl -s -w "\n%{http_code}" "$RAILWAY_URL/api/health")
http_code=$(echo "$response" | tail -n1)
body=$(echo "$response" | head -n-1)

if [ "$http_code" -eq 200 ]; then
    echo -e "${GREEN}✅ Servidor activo${NC}"
    echo "   $body"
else
    echo -e "${RED}❌ Servidor no responde (HTTP $http_code)${NC}"
    exit 1
fi
echo ""

# 2. Verificar CORS Headers
echo -e "${YELLOW}2. Verificar CORS${NC}"
cors_headers=$(curl -s -I -X OPTIONS "$RAILWAY_URL/api/auth/courier/login" \
    -H "Origin: https://casilleroback-production.up.railway.app" \
    -H "Access-Control-Request-Method: POST" | grep -i "access-control")

if [ -n "$cors_headers" ]; then
    echo -e "${GREEN}✅ CORS configurado${NC}"
    echo "$cors_headers"
else
    echo -e "${RED}❌ CORS no configurado - código no desplegado${NC}"
    echo "   Solución: Verifica que el último deployment haya sido exitoso"
fi
echo ""

# 3. Test Login COUR001
echo -e "${YELLOW}3. Test Login - COUR001 (PIN: 1234)${NC}"
response=$(curl -s -w "\n%{http_code}" -X POST "$RAILWAY_URL/api/auth/courier/login" \
    -H "Content-Type: application/json" \
    -d '{"employeeId": "COUR001", "pin": "1234"}')
http_code=$(echo "$response" | tail -n1)
body=$(echo "$response" | head -n-1)

if [ "$http_code" -eq 200 ]; then
    echo -e "${GREEN}✅ Login exitoso${NC}"
    token=$(echo "$body" | grep -o '"token":"[^"]*' | cut -d'"' -f4 | cut -c1-30)
    echo "   Token: ${token}..."
else
    echo -e "${RED}❌ Login falló (HTTP $http_code)${NC}"
    echo "   $body"
fi
echo ""

# 4. Test Login COUR002
echo -e "${YELLOW}4. Test Login - COUR002 (PIN: 5678)${NC}"
response=$(curl -s -w "\n%{http_code}" -X POST "$RAILWAY_URL/api/auth/courier/login" \
    -H "Content-Type: application/json" \
    -d '{"employeeId": "COUR002", "pin": "5678"}')
http_code=$(echo "$response" | tail -n1)
body=$(echo "$response" | head -n-1)

if [ "$http_code" -eq 200 ]; then
    echo -e "${GREEN}✅ Login exitoso - Migración V6 aplicada${NC}"
    token=$(echo "$body" | grep -o '"token":"[^"]*' | cut -d'"' -f4 | cut -c1-30)
    echo "   Token: ${token}..."
else
    echo -e "${RED}❌ Login falló (HTTP $http_code)${NC}"
    echo "   $body"
    echo -e "${YELLOW}   Posible causa: Migración V6 no aplicada${NC}"
fi
echo ""

# 5. Test PIN Incorrecto
echo -e "${YELLOW}5. Test PIN Incorrecto${NC}"
response=$(curl -s -w "\n%{http_code}" -X POST "$RAILWAY_URL/api/auth/courier/login" \
    -H "Content-Type: application/json" \
    -d '{"employeeId": "COUR001", "pin": "9999"}')
http_code=$(echo "$response" | tail -n1)

if [ "$http_code" -ne 200 ]; then
    echo -e "${GREEN}✅ Validación de PIN funciona${NC}"
else
    echo -e "${RED}❌ Validación no funciona${NC}"
fi
echo ""

# 6. Test Formato Inválido
echo -e "${YELLOW}6. Test Formato Inválido (PIN: abc)${NC}"
response=$(curl -s -w "\n%{http_code}" -X POST "$RAILWAY_URL/api/auth/courier/login" \
    -H "Content-Type: application/json" \
    -d '{"employeeId": "COUR001", "pin": "abc"}')
http_code=$(echo "$response" | tail -n1)

if [ "$http_code" -eq 400 ]; then
    echo -e "${GREEN}✅ Validación de formato funciona${NC}"
else
    echo -e "${YELLOW}⚠️  Validación de formato no aplicada (HTTP $http_code)${NC}"
fi
echo ""

echo "===================================="
echo -e "${GREEN}✅ Verificación completada${NC}"
echo ""
echo "Swagger UI: $RAILWAY_URL/swagger-ui.html"
