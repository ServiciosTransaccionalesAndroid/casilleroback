# 📋 Documentación de Cambios Implementados

## 🆕 Cambios Realizados

### 1. Campo `description` en Paquetes
### 2. Validación de PIN Secreto en Retiros
### 3. Configuración para Railway

---

## 📦 1. Campo Description en Paquetes

### Archivos Modificados:

#### `Package.java` (Entidad)
```java
@Column(columnDefinition = "TEXT")
private String description;
```

#### `PackageDTO.java` (DTOs)
```java
// CreateRequest
public record CreateRequest(
    @NotBlank String trackingNumber,
    @NotNull Long recipientId,
    @Positive BigDecimal width,
    @Positive BigDecimal height,
    @Positive BigDecimal depth,
    @Positive BigDecimal weight,
    String description  // ← NUEVO
) {}

// UpdateRequest
public record UpdateRequest(
    Long recipientId,
    BigDecimal width,
    BigDecimal height,
    BigDecimal depth,
    BigDecimal weight,
    String description,  // ← NUEVO
    String status
) {}

// PackageResponse
public record PackageResponse(
    Long id,
    String trackingNumber,
    RecipientDTO.RecipientResponse recipient,
    BigDecimal width,
    BigDecimal height,
    BigDecimal depth,
    BigDecimal weight,
    String description,  // ← NUEVO
    String status
) {}
```

#### Migración de Base de Datos
**Archivo:** `V12__add_description_to_packages.sql`
```sql
ALTER TABLE packages ADD COLUMN description TEXT;
```

### Uso:

```json
POST /api/packages
{
  "trackingNumber": "PKG123",
  "recipientId": 1,
  "width": 30.5,
  "height": 20.0,
  "depth": 15.0,
  "weight": 2.5,
  "description": "Laptop Dell XPS 15, color negro"
}
```

---

## 🔐 2. Validación de PIN Secreto en Retiros

### Archivos Modificados:

#### `RetrievalDTO.java`
```java
public record RetrievalRequest(
    @NotBlank(message = "Code is required")
    String code,
    
    @NotBlank(message = "Secret PIN is required")  // ← NUEVO
    String secretPin,
    
    String photoUrl
) {}
```

#### `RetrievalService.java`
```java
@Transactional
public RetrievalResult processRetrieval(String code, String secretPin, String photoUrl) {
    // Validar código
    RetrievalCode retrievalCode = retrievalCodeService.validateCode(code);
    if (retrievalCode == null) {
        throw new RuntimeException("Invalid or expired retrieval code: " + code);
    }

    // Validar PIN secreto ← NUEVO
    if (!retrievalCodeService.validateSecretPin(code, secretPin)) {
        log.warn("Invalid secret PIN for code: {}", code);
        throw new RuntimeException("Invalid secret PIN");
    }

    // ... resto del proceso
}
```

#### `RetrievalController.java`
```java
@PostMapping
public ResponseEntity<RetrievalDTO.RetrievalResponse> processRetrieval(
        @Valid @RequestBody RetrievalDTO.RetrievalRequest request) {
    
    RetrievalService.RetrievalResult result = retrievalService.processRetrieval(
        request.code(),
        request.secretPin(),  // ← NUEVO
        request.photoUrl()
    );
    
    // ... resto del código
}
```

### Uso:

**Antes:**
```json
POST /api/retrievals
{
  "code": "RCSV2X4Y",
  "photoUrl": "https://..."
}
```

**Ahora (OBLIGATORIO):**
```json
POST /api/retrievals
{
  "code": "RCSV2X4Y",
  "secretPin": "123456",
  "photoUrl": "https://..."
}
```

### Respuestas:

✅ **Éxito:**
```json
{
  "retrievalId": 1,
  "timestamp": "2024-01-15T10:30:00",
  "message": "Package retrieved successfully"
}
```

❌ **PIN Incorrecto:**
```json
{
  "error": "Invalid secret PIN"
}
```

❌ **Código Inválido:**
```json
{
  "error": "Invalid or expired retrieval code"
}
```

---

## 🚂 3. Configuración para Railway

### Archivos Creados:

#### `railway.json`
```json
{
  "$schema": "https://railway.app/railway.schema.json",
  "build": {
    "builder": "DOCKERFILE",
    "dockerfilePath": "Dockerfile"
  },
  "deploy": {
    "startCommand": "java -jar app.jar",
    "restartPolicyType": "ON_FAILURE",
    "restartPolicyMaxRetries": 10
  }
}
```

#### `Procfile`
```
web: java -jar target/*.jar
```

#### `application-prod.yml` (Actualizado)
```yaml
spring:
  datasource:
    url: ${DATABASE_URL:jdbc:postgresql://localhost:5432/locker_db}
    username: ${DATABASE_USERNAME:locker_user}
    password: ${DATABASE_PASSWORD:locker_pass}
  jpa:
    show-sql: false
    hibernate:
      ddl-auto: validate

server:
  port: ${PORT:8080}

logging:
  level:
    com.servientrega.locker: INFO
    org.springframework.web: WARN
    org.hibernate.SQL: WARN
```

---

## 🔧 Instrucciones de Despliegue

### Opción 1: Despliegue Local (Docker)

```bash
# 1. Aplicar cambios
git pull

# 2. Reiniciar servicios
docker-compose down
docker-compose up -d --build

# 3. Verificar logs
docker-compose logs -f backend

# 4. Probar
curl http://localhost:8080/api/health
```

### Opción 2: Despliegue en Railway

#### Paso 1: Configurar Variables de Entorno

En Railway, servicio **backend** → **Variables**:

```bash
# Base de Datos (OBLIGATORIO)
SPRING_PROFILES_ACTIVE=prod
SPRING_DATASOURCE_URL=jdbc:postgresql://${{Postgres.RAILWAY_PRIVATE_DOMAIN}}:5432/${{Postgres.POSTGRES_DB}}
SPRING_DATASOURCE_USERNAME=${{Postgres.POSTGRES_USER}}
SPRING_DATASOURCE_PASSWORD=${{Postgres.POSTGRES_PASSWORD}}

# JWT (OBLIGATORIO)
JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
JWT_EXPIRATION=28800000

# Códigos de Retiro (OBLIGATORIO)
RETRIEVAL_CODE_LENGTH=8
RETRIEVAL_CODE_EXPIRATION_HOURS=48

# Email (OPCIONAL - para notificaciones)
SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=quasar1035@gmail.com
SPRING_MAIL_PASSWORD=kcrg pabt ldzs mady
```

#### Paso 2: Desplegar

```bash
# 1. Commit y push
git add .
git commit -m "Add description field and PIN validation"
git push origin main

# 2. Railway desplegará automáticamente
# 3. Esperar 3-5 minutos

# 4. Verificar
curl https://tu-dominio.up.railway.app/api/health
```

#### Paso 3: Verificar Migraciones

Railway ejecutará automáticamente Flyway. En los logs deberías ver:

```
Flyway migration V12__add_description_to_packages.sql completed successfully
Started LockerApplication in X seconds
```

---

## 🧪 Testing de Cambios

### Test 1: Campo Description

```bash
# Crear paquete con descripción
POST /api/packages
{
  "trackingNumber": "TEST001",
  "recipientId": 1,
  "width": 30,
  "height": 20,
  "depth": 15,
  "weight": 2.5,
  "description": "Producto de prueba"
}

# Verificar respuesta incluye description
GET /api/packages/TEST001
```

### Test 2: PIN Secreto

```bash
# 1. Depositar paquete (genera código + PIN)
POST /api/deposits
{
  "trackingNumber": "TEST001",
  "lockerId": 1,
  "courierEmployeeId": "EMP001"
}

# Respuesta incluye:
{
  "retrievalCode": "RCSV2X4Y",
  "secretPin": "123456"
}

# 2. Intentar retiro SIN PIN (debe fallar)
POST /api/retrievals
{
  "code": "RCSV2X4Y"
}
# Error: "Secret PIN is required"

# 3. Intentar retiro con PIN incorrecto (debe fallar)
POST /api/retrievals
{
  "code": "RCSV2X4Y",
  "secretPin": "000000"
}
# Error: "Invalid secret PIN"

# 4. Retiro con PIN correcto (debe funcionar)
POST /api/retrievals
{
  "code": "RCSV2X4Y",
  "secretPin": "123456"
}
# Success: "Package retrieved successfully"
```

### Test 3: Email con PIN

```bash
# Verificar que el email incluye el PIN
# El destinatario debe recibir un correo con:
# - Código: RCSV2X4Y
# - PIN: 123456
# - QR adjunto
```

---

## 📊 Resumen de Endpoints Afectados

### Modificados:

| Endpoint | Cambio | Impacto |
|----------|--------|---------|
| `POST /api/packages` | Campo `description` opcional | Backward compatible |
| `PUT /api/packages/{id}` | Campo `description` opcional | Backward compatible |
| `GET /api/packages/{id}` | Respuesta incluye `description` | Backward compatible |
| `POST /api/retrievals` | Campo `secretPin` OBLIGATORIO | ⚠️ BREAKING CHANGE |

### Sin Cambios:

- `GET /api/retrievals/validate`
- `POST /api/deposits`
- Todos los demás endpoints

---

## ⚠️ Breaking Changes

### ⚠️ POST /api/retrievals

**Antes:**
```json
{
  "code": "RCSV2X4Y"
}
```

**Ahora (OBLIGATORIO):**
```json
{
  "code": "RCSV2X4Y",
  "secretPin": "123456"
}
```

**Acción Requerida:**
- Actualizar clientes/frontend para incluir `secretPin`
- El PIN se obtiene del email o de la respuesta del depósito

---

## 🔄 Rollback (Si es necesario)

### Revertir Cambios:

```bash
# 1. Revertir commits
git revert HEAD~3..HEAD

# 2. Revertir migración de BD (manual)
ALTER TABLE packages DROP COLUMN description;

# 3. Redesplegar
git push origin main
```

---

## 📞 Soporte

### Problemas Comunes:

**1. Error: "Secret PIN is required"**
- Solución: Agregar campo `secretPin` en el request

**2. Error: "Invalid secret PIN"**
- Solución: Verificar que el PIN sea correcto (6 dígitos)

**3. Migración no se ejecuta**
- Solución: Verificar que Flyway esté habilitado
- Revisar logs: `docker-compose logs -f backend`

**4. Email no se envía**
- Solución: Configurar variables de email en Railway
- Verificar credenciales de Gmail

---

## ✅ Checklist de Despliegue

- [ ] Variables de entorno configuradas en Railway
- [ ] PostgreSQL vinculado con backend
- [ ] Código pusheado a GitHub
- [ ] Railway desplegó exitosamente
- [ ] Migración V12 ejecutada
- [ ] Health check funciona
- [ ] Swagger UI accesible
- [ ] Test de creación de paquete con description
- [ ] Test de retiro con PIN
- [ ] Email con PIN recibido
- [ ] Frontend actualizado para incluir secretPin

---

**Última actualización:** Enero 2025  
**Versión:** 1.1.0
