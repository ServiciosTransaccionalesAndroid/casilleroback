# 🚀 Desplegar Cambios en Railway

## ✅ Cambios Realizados

1. **Sistema de PINs mejorado** - Múltiples usuarios con PINs diferentes
2. **CORS habilitado** - Permite peticiones desde Swagger UI y otros orígenes

---

## 📦 Pasos para Desplegar

### 1. Commit y Push
```bash
git add .
git commit -m "feat: sistema de PINs mejorado y CORS habilitado"
git push origin main
```

### 2. Railway Desplegará Automáticamente
- Railway detecta el push y inicia el build
- Espera 2-3 minutos

### 3. Verificar Variables de Entorno en Railway

Asegúrate que el servicio **backend** tenga estas variables:

```bash
SPRING_PROFILES_ACTIVE=prod
SPRING_DATASOURCE_URL=${{Postgres.DATABASE_URL}}
SPRING_DATASOURCE_USERNAME=${{Postgres.PGUSER}}
SPRING_DATASOURCE_PASSWORD=${{Postgres.PGPASSWORD}}
JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
JWT_EXPIRATION=28800000
RETRIEVAL_CODE_LENGTH=8
RETRIEVAL_CODE_EXPIRATION_HOURS=48
```

### 4. Aplicar Migraciones (Automático)
Flyway aplicará automáticamente la migración V6 con los nuevos PINs.

---

## 🧪 Probar en Railway

### Opción 1: Swagger UI
```
https://casilleroback-production.up.railway.app/swagger-ui.html
```

1. Ir a `POST /api/auth/courier/login`
2. Click "Try it out"
3. Probar con:
```json
{
  "employeeId": "COUR002",
  "pin": "5678"
}
```

### Opción 2: cURL
```bash
curl -X POST https://casilleroback-production.up.railway.app/api/auth/courier/login \
  -H "Content-Type: application/json" \
  -d '{"employeeId": "COUR002", "pin": "5678"}'
```

---

## 👥 Usuarios Disponibles en Railway

| Employee ID | PIN |
|-------------|-----|
| COUR001 | 1234 |
| COUR002 | 5678 |
| COUR003 | 9012 |
| COUR004 | 123456 |

---

## ✅ Verificar que Funciona

### 1. Health Check
```bash
curl https://casilleroback-production.up.railway.app/api/health
```

### 2. Login Exitoso
```bash
curl -X POST https://casilleroback-production.up.railway.app/api/auth/courier/login \
  -H "Content-Type: application/json" \
  -d '{"employeeId": "COUR001", "pin": "1234"}'
```

**Respuesta esperada:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "employeeId": "COUR001",
  "name": "Juan Pérez",
  "message": "Login successful"
}
```

### 3. PIN Incorrecto
```bash
curl -X POST https://casilleroback-production.up.railway.app/api/auth/courier/login \
  -H "Content-Type: application/json" \
  -d '{"employeeId": "COUR001", "pin": "9999"}'
```

**Respuesta esperada:**
```json
{
  "error": "Invalid PIN"
}
```

---

## 🔍 Ver Logs en Railway

1. Ve al servicio **backend**
2. Click en pestaña **"Deployments"**
3. Click en el último deployment
4. Revisa los logs para ver:
   - ✅ Flyway migration V6 completed
   - ✅ Started LockerApplication

---

## 🚨 Si Hay Problemas

### Error: "Connection refused"
- Verifica que PostgreSQL esté vinculado
- Revisa las variables de entorno

### Error: "CORS"
- Ya está solucionado con CorsConfig.java
- Si persiste, verifica que el código se haya desplegado

### Error: "Invalid PIN" con PIN correcto
- La migración V6 no se aplicó
- Solución: Elimina y recrea la BD en Railway (⚠️ perderás datos)

---

## 📝 Archivos Modificados

- ✅ `AuthDTO.java` - Validación de PIN
- ✅ `AuthController.java` - Verificación habilitada
- ✅ `SecurityConfig.java` - CORS habilitado
- ✅ `CorsConfig.java` - Configuración de CORS (NUEVO)
- ✅ `V6__update_courier_pins.sql` - Nuevos PINs (NUEVO)

---

## 🎯 Resultado Final

Después del deploy, podrás:
- ✅ Usar Swagger UI sin errores de CORS
- ✅ Login con diferentes usuarios y PINs
- ✅ Validación de formato de PIN
- ✅ Mensajes de error claros


---

## 🐳 Error de Docker: TLS Handshake Timeout

### Síntoma
```
ERROR: failed to resolve source metadata for docker.io/library/eclipse-temurin
TLS handshake timeout
```

### Causa
Railway tiene problemas temporales conectándose a Docker Hub.

### Solución 1: Reintentar Deploy
1. Ve a **Deployments** en Railway
2. Click en **"Redeploy"** en el deployment fallido
3. Espera 2-3 minutos

### Solución 2: Dockerfile Actualizado
Ya se actualizó el Dockerfile para usar `maven:3.9-eclipse-temurin-17-alpine` que es más confiable.

### Solución 3: Usar Amazon Corretto (Alternativa)
Si persiste el error, cambia el Dockerfile:

```bash
# Renombrar archivos
mv Dockerfile Dockerfile.backup
mv Dockerfile.corretto Dockerfile

# Commit y push
git add .
git commit -m "fix: usar Amazon Corretto como imagen base"
git push origin main
```

### Solución 4: Esperar y Reintentar
A veces es un problema temporal de Docker Hub. Espera 10-15 minutos y reintenta.
