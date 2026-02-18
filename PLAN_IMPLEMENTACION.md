# Plan de Implementación - Backend Casilleros Servientrega

## Información del Proyecto

**Tecnologías:** Java 17+, Spring Boot 3.x, PostgreSQL, Docker, Redis  
**Arquitectura:** REST API  
**Duración Estimada:** 19 días  
**Tipo:** Sistema de gestión de lockers inteligentes

---

## Fase 0: Configuración Inicial del Proyecto ✅
**Duración:** 1 día  
**Prioridad:** Alta  
**Estado:** COMPLETADA

### Objetivos
- Estructura base del proyecto Spring Boot
- Configuración Docker y PostgreSQL
- Configuración de dependencias Maven

### Tareas
1. Crear proyecto Spring Boot con Spring Initializr
2. Configurar `pom.xml` con dependencias necesarias
3. Crear `Dockerfile` para la aplicación
4. Crear `docker-compose.yml` con PostgreSQL
5. Configurar `application.yml` con perfiles (dev, prod)
6. Estructura de carpetas del proyecto

### Entregables
- `pom.xml` con dependencias
- `Dockerfile` y `docker-compose.yml`
- Estructura de carpetas
- `application.yml` configurado
- Proyecto ejecutándose en Docker

### Dependencias Maven
- spring-boot-starter-web
- spring-boot-starter-data-jpa
- spring-boot-starter-security
- spring-boot-starter-validation
- postgresql
- lombok
- springdoc-openapi (Swagger)
- flyway-core

---

## Fase 1: Modelo de Datos y Entidades ✅
**Duración:** 2 días  
**Prioridad:** Alta  
**Dependencias:** Fase 0  
**Estado:** COMPLETADA

### Objetivos
- Diseñar esquema de base de datos PostgreSQL
- Crear entidades JPA
- Configurar migraciones con Flyway

### Entidades Principales

#### 1. Locker
- id, name, location, address, latitude, longitude
- status (ACTIVE, INACTIVE, MAINTENANCE)
- totalCompartments

#### 2. Compartment
- id, lockerId, compartmentNumber, size (SMALL, MEDIUM, LARGE)
- status (DISPONIBLE, OCUPADO, ABIERTO, MANTENIMIENTO)
- sensorReadings (JSON)

#### 3. Package
- id, trackingNumber, recipientName, recipientPhone, recipientEmail
- dimensions (width, height, depth, weight)
- status (EN_TRANSITO, EN_LOCKER, ENTREGADO)

#### 4. Deposit
- id, packageId, compartmentId, courierId
- depositTimestamp, photoUrl

#### 5. Retrieval
- id, depositId, retrievalCodeId
- retrievalTimestamp, photoUrl

#### 6. RetrievalCode
- id, code (6-8 caracteres alfanuméricos)
- depositId, generatedAt, expiresAt
- used (boolean), usedAt

#### 7. StatusHistory
- id, compartmentId, previousState, currentState
- timestamp, sensorReadings (JSON)

#### 8. Courier
- id, employeeId, name, phone, email
- active (boolean)

#### 9. Alert
- id, lockerId, compartmentId, alertType
- severity (INFO, WARNING, CRITICAL)
- message, createdAt, resolvedAt

### Tareas
1. Crear scripts de migración Flyway (V1__initial_schema.sql)
2. Crear clases de entidad JPA con anotaciones
3. Crear repositorios Spring Data JPA
4. Crear enums (CompartmentStatus, PackageStatus, AlertType, etc.)

### Entregables
- Scripts SQL de migración en `src/main/resources/db/migration/`
- Clases de entidad en `com.servientrega.locker.entity`
- Repositorios en `com.servientrega.locker.repository`
- Enums en `com.servientrega.locker.enums`

---

## Fase 2: Servicios Core (Simulación ERP) ✅
**Duración:** 3 días  
**Prioridad:** Alta  
**Dependencias:** Fase 1  
**Estado:** COMPLETADA

### Objetivos
- Implementar servicios de negocio principales
- Simular integración con ERP de Servientrega
- Lógica de generación de códigos de retiro
- Algoritmo de asignación inteligente de casilleros

### Componentes

#### 1. ErpSimulatorService
Simula respuestas del ERP de Servientrega
- `validatePackage(trackingNumber)` → Retorna datos del paquete
- `updatePackageStatus(trackingNumber, status)` → Simula actualización
- Datos mock predefinidos para testing

#### 2. PackageService
- `validatePackage(trackingNumber)` → Valida contra ERP simulado
- `getPackageInfo(trackingNumber)` → Obtiene información completa
- `updatePackageStatus(trackingNumber, status)` → Actualiza estado

#### 3. RetrievalCodeService
- `generateCode()` → Genera código alfanumérico único (6-8 chars)
- `validateCode(code)` → Verifica validez, expiración, uso
- `markAsUsed(code)` → Marca código como usado
- Configuración de vigencia (24-72 horas)

#### 4. CompartmentService
- `findAvailableCompartments(lockerId)` → Lista casilleros disponibles
- `assignCompartment(packageDimensions)` → Algoritmo de asignación inteligente
- `updateCompartmentStatus(compartmentId, status)` → Actualiza estado
- `getCompartmentsByLocker(lockerId)` → Lista todos los casilleros

#### 5. DepositService
- `processDeposit(depositRequest)` → Procesa depósito completo
- `generateRetrievalCode(depositId)` → Genera código de retiro
- `recordDeposit(trackingNumber, compartmentId, courierId)` → Registra en BD

#### 6. RetrievalService
- `validateRetrievalCode(code)` → Valida código de retiro
- `processRetrieval(code)` → Procesa retiro completo
- `recordRetrieval(code, compartmentId)` → Registra en BD

### Tareas
1. Crear servicios en `com.servientrega.locker.service`
2. Implementar ErpSimulatorService con datos mock
3. Implementar algoritmo de asignación inteligente de casilleros
4. Implementar generador de códigos alfanuméricos únicos
5. Crear tests unitarios para cada servicio

### Entregables
- Servicios implementados con lógica de negocio
- ErpSimulatorService funcional con datos de prueba
- Tests unitarios (JUnit 5 + Mockito)
- Documentación de algoritmos

---

## Fase 3: API REST - Endpoints Principales ✅
**Duración:** 3 días  
**Prioridad:** Alta  
**Dependencias:** Fase 2  
**Estado:** COMPLETADA

### Objetivos
- Implementar controladores REST
- Validación de requests con Bean Validation
- Manejo de excepciones global
- Documentación con Swagger/OpenAPI

### Endpoints a Implementar

#### Gestión de Paquetes
```
GET /api/packages/validate?trackingNumber={number}
```
**Descripción:** Valida paquete contra ERP simulado  
**Response:** PackageInfo (trackingNumber, dimensions, recipientInfo)

#### Gestión de Depósitos
```
POST /api/deposits
Body: {
  "trackingNumber": "string",
  "lockerId": "long",
  "compartmentId": "long",
  "courierId": "long",
  "photoUrl": "string"
}
```
**Descripción:** Registra depósito de paquete  
**Response:** DepositResponse (depositId, retrievalCode, expiresAt)

#### Gestión de Retiros
```
GET /api/retrievals/validate?code={code}
```
**Descripción:** Valida código de retiro  
**Response:** RetrievalValidation (valid, compartmentId, expiresAt)

```
POST /api/retrievals
Body: {
  "code": "string",
  "compartmentId": "long",
  "photoUrl": "string"
}
```
**Descripción:** Registra retiro de paquete  
**Response:** RetrievalResponse (retrievalId, timestamp)

#### Gestión de Lockers
```
POST /api/lockers/status-update
Body: {
  "lockerId": "long",
  "compartmentId": "long",
  "previousState": "string",
  "currentState": "string",
  "timestamp": "datetime",
  "sensorReadings": {
    "sensor1": boolean,
    "sensor2": boolean,
    "sensor3": boolean,
    "sensor4": boolean,
    "infrared": boolean
  }
}
```
**Descripción:** Actualiza estado de casillero desde software propietario

```
GET /api/lockers/{lockerId}/status
```
**Descripción:** Consulta estado general del locker

```
GET /api/lockers/{lockerId}/compartments
```
**Descripción:** Lista casilleros y su disponibilidad

#### Autenticación
```
POST /api/auth/courier/login
Body: {
  "employeeId": "string",
  "pin": "string"
}
```
**Descripción:** Login de mensajero  
**Response:** AuthResponse (token, courierInfo)

### Tareas
1. Crear controladores en `com.servientrega.locker.controller`
2. Crear DTOs de request/response en `com.servientrega.locker.dto`
3. Implementar validaciones con Bean Validation (@Valid, @NotNull, etc.)
4. Crear GlobalExceptionHandler para manejo de errores
5. Configurar Swagger/OpenAPI
6. Crear tests de integración para endpoints

### Entregables
- Controladores REST implementados
- DTOs con validaciones
- GlobalExceptionHandler
- Documentación Swagger en `/swagger-ui.html`
- Tests de integración

---

## Fase 4: Sistema de Notificaciones (Simulado) ✅
**Duración:** 2 días  
**Prioridad:** Media  
**Dependencias:** Fase 2  
**Estado:** COMPLETADA

### Objetivos
- Implementar servicio de notificaciones
- Simular envío de SMS y Email (logs en consola)
- Sistema de plantillas de mensajes

### Componentes

#### 1. NotificationService
Servicio principal que coordina notificaciones
- `sendRetrievalCodeNotification(recipientInfo, code, lockerInfo)`
- `sendExpirationAlert(recipientInfo, code)`
- `sendDeliveryConfirmation(recipientInfo, trackingNumber)`
- `sendMaintenanceAlert(technicianInfo, lockerInfo)`

#### 2. SmsSimulatorService
Simula envío de SMS (logs en consola)
- `sendSms(phoneNumber, message)`
- Log formato: `[SMS SENT] To: +57... | Message: ...`

#### 3. EmailSimulatorService
Simula envío de Email (logs en consola)
- `sendEmail(email, subject, body)`
- Log formato: `[EMAIL SENT] To: ... | Subject: ...`

#### 4. TemplateService
Gestión de plantillas de mensajes
- `getRetrievalCodeTemplate(code, location, expiresAt)`
- `getExpirationAlertTemplate(code, hoursRemaining)`
- `getDeliveryConfirmationTemplate(trackingNumber)`

### Tipos de Notificaciones
1. **Código de retiro generado** (SMS + Email)
2. **Alerta de vencimiento** (SMS + Email)
3. **Confirmación de entrega** (SMS + Email)
4. **Alertas de mantenimiento** (Email a técnicos)

### Tareas
1. Crear servicios en `com.servientrega.locker.service.notification`
2. Implementar simuladores con logs estructurados
3. Crear plantillas de mensajes
4. Integrar con DepositService y RetrievalService
5. Configurar logging con SLF4J

### Entregables
- NotificationService implementado
- Simuladores de SMS y Email funcionales
- Sistema de plantillas
- Logs estructurados en consola
- Tests unitarios

---

## Fase 5: Gestión de Alertas y Monitoreo ✅
**Duración:** 2 días  
**Prioridad:** Media  
**Dependencias:** Fase 2  
**Estado:** COMPLETADA

### Objetivos
- Sistema de detección de alertas
- Registro de eventos de mantenimiento
- Métricas operacionales básicas

### Componentes

#### 1. AlertService
- `createAlert(lockerId, compartmentId, alertType, severity, message)`
- `getActiveAlerts(lockerId)`
- `resolveAlert(alertId)`
- Tipos: MAINTENANCE, SENSOR_ERROR, DOOR_STUCK, TIMEOUT

#### 2. MaintenanceService
- `detectMaintenanceNeeded(statusUpdate)` → Analiza cambios de estado
- `scheduleMaintenanceTicket(compartmentId)`
- `getCompartmentsInMaintenance(lockerId)`

#### 3. MetricsService
- `getLockerMetrics(lockerId)` → Ocupación, disponibilidad
- `getOperationalMetrics()` → Total depósitos, retiros, tiempo promedio
- `getCompartmentUtilization(lockerId)` → Uso por tamaño

### Tareas
1. Crear servicios en `com.servientrega.locker.service.monitoring`
2. Implementar lógica de detección de alertas
3. Crear endpoints de métricas
4. Integrar con StatusHistoryService
5. Dashboard básico (opcional)

### Entregables
- AlertService implementado
- MaintenanceService funcional
- Endpoints de métricas
- Tests unitarios

---

## Fase 6: Seguridad y Autenticación ✅
**Duración:** 2 días  
**Prioridad:** Alta  
**Dependencias:** Fase 3  
**Estado:** COMPLETADA

### Objetivos
- Implementar Spring Security
- JWT para autenticación
- Roles y permisos (COURIER, ADMIN, SYSTEM)

### Componentes

#### 1. JwtService
- `generateToken(userDetails)` → Genera JWT
- `validateToken(token)` → Valida JWT
- `extractUsername(token)` → Extrae información
- Configuración de expiración (8 horas)

#### 2. SecurityConfig
- Configuración de Spring Security
- Endpoints públicos vs protegidos
- CORS configuration
- Password encoding (BCrypt)

#### 3. AuthenticationService
- `authenticateCourier(employeeId, pin)` → Login mensajero
- `authenticateAdmin(username, password)` → Login admin
- `refreshToken(token)` → Renovar token

### Roles y Permisos
- **COURIER:** Depósitos, consulta de casilleros
- **ADMIN:** Gestión completa, reportes, configuración
- **SYSTEM:** Actualizaciones de estado desde software propietario

### Tareas
1. Crear servicios en `com.servientrega.locker.security`
2. Implementar JwtService con jjwt library
3. Configurar Spring Security
4. Crear filtros de autenticación JWT
5. Proteger endpoints según roles
6. Tests de seguridad

### Entregables
- Sistema de autenticación JWT funcional
- Endpoints protegidos por roles
- SecurityConfig completo
- Tests de seguridad

---

## Fase 7: Caché con Redis (Opcional)
**Duración:** 1 día  
**Prioridad:** Baja  
**Dependencias:** Fase 3

### Objetivos
- Configurar Redis en Docker
- Cachear códigos de retiro
- Cachear estado de casilleros

### Componentes
1. Configuración Redis en docker-compose
2. Spring Data Redis
3. Anotaciones @Cacheable en servicios críticos

### Datos a Cachear
- Códigos de retiro activos (TTL = tiempo de expiración)
- Estado de casilleros por locker
- Información de paquetes validados

### Tareas
1. Agregar Redis a docker-compose.yml
2. Configurar spring-boot-starter-data-redis
3. Implementar @Cacheable en servicios
4. Configurar TTL por tipo de dato
5. Tests de caché

### Entregables
- Redis configurado en Docker
- Caché implementado en servicios críticos
- Configuración de TTL
- Tests de caché

---

## Fase 8: Testing y Documentación
**Duración:** 2 días  
**Prioridad:** Alta  
**Dependencias:** Todas las fases anteriores

### Objetivos
- Tests de integración completos
- Tests de endpoints
- Documentación técnica

### Tipos de Tests

#### 1. Tests Unitarios
- Servicios de negocio
- Validaciones
- Algoritmos
- Cobertura objetivo: >70%

#### 2. Tests de Integración
- Endpoints REST
- Flujos completos (depósito + retiro)
- Base de datos con TestContainers
- Seguridad y autenticación

#### 3. Tests de Carga (Opcional)
- JMeter o Gatling
- Simular múltiples lockers
- Concurrencia en depósitos/retiros

### Documentación

#### 1. README.md
- Descripción del proyecto
- Requisitos previos
- Instrucciones de instalación
- Comandos Docker
- Variables de entorno

#### 2. API_DOCUMENTATION.md
- Descripción de endpoints
- Ejemplos de requests/responses
- Códigos de error
- Flujos de negocio

#### 3. ARCHITECTURE.md
- Diagrama de arquitectura
- Modelo de datos
- Decisiones técnicas

### Tareas
1. Completar tests unitarios (JUnit 5 + Mockito)
2. Crear tests de integración con TestContainers
3. Configurar Jacoco para cobertura
4. Escribir README.md completo
5. Documentar API
6. Crear colección Postman/Insomnia
7. Diagramas de arquitectura

### Entregables
- Tests unitarios (>70% cobertura)
- Tests de integración funcionales
- README.md completo
- API_DOCUMENTATION.md
- Colección Postman
- Diagramas de arquitectura

---

## Fase 9: Dockerización y Despliegue
**Duración:** 1 día  
**Prioridad:** Alta  
**Dependencias:** Todas las fases anteriores

### Objetivos
- Optimizar Dockerfile
- Configurar docker-compose completo
- Scripts de inicialización
- Preparar para producción

### Componentes Docker

#### 1. Backend Spring Boot
- Dockerfile multi-stage
- Imagen base: eclipse-temurin:17-jre-alpine
- Optimización de capas

#### 2. PostgreSQL
- Imagen oficial postgres:15-alpine
- Volumen persistente
- Scripts de inicialización

#### 3. Redis (Opcional)
- Imagen oficial redis:7-alpine
- Configuración de persistencia

#### 4. Adminer/PgAdmin (Opcional)
- Herramienta de administración de BD

### docker-compose.yml Completo
```yaml
services:
  - backend (Spring Boot)
  - postgres (PostgreSQL)
  - redis (Redis)
  - adminer (Opcional)
```

### Scripts de Inicialización
1. `init-db.sql` → Datos de prueba
2. `start.sh` → Script de inicio
3. `stop.sh` → Script de parada
4. `logs.sh` → Ver logs

### Tareas
1. Crear Dockerfile multi-stage optimizado
2. Completar docker-compose.yml
3. Crear scripts de inicialización
4. Configurar variables de entorno
5. Crear datos de prueba (lockers, couriers, packages)
6. Documentar comandos Docker
7. Probar despliegue completo

### Entregables
- Dockerfile optimizado
- docker-compose.yml completo
- Scripts de inicialización
- Datos de prueba
- Documentación de despliegue
- Sistema funcionando con un comando

---

## Resumen de Fases

| Fase | Duración | Prioridad | Dependencias |
|------|----------|-----------|--------------|
| 0 - Setup | 1 día | Alta | - |
| 1 - Modelo de Datos | 2 días | Alta | Fase 0 |
| 2 - Servicios Core | 3 días | Alta | Fase 1 |
| 3 - API REST | 3 días | Alta | Fase 2 |
| 4 - Notificaciones | 2 días | Media | Fase 2 |
| 5 - Alertas | 2 días | Media | Fase 2 |
| 6 - Seguridad | 2 días | Alta | Fase 3 |
| 7 - Redis | 1 día | Baja | Fase 3 |
| 8 - Testing | 2 días | Alta | Todas |
| 9 - Docker | 1 día | Alta | Todas |

**Duración Total: 19 días**

---

## Stack Tecnológico

### Backend
- Java 17+
- Spring Boot 3.x
- Spring Data JPA
- Spring Security
- Spring Validation

### Base de Datos
- PostgreSQL 15
- Flyway (migraciones)

### Caché
- Redis 7 (opcional)

### Seguridad
- JWT (JSON Web Tokens)
- BCrypt (password hashing)

### Documentación
- Swagger/OpenAPI 3
- SpringDoc

### Testing
- JUnit 5
- Mockito
- TestContainers
- Jacoco (cobertura)

### Build & Deploy
- Maven
- Docker
- Docker Compose

### Utilidades
- Lombok
- MapStruct (opcional)
- SLF4J + Logback

---

## Simulaciones Implementadas

Todo lo relacionado con Servientrega será simulado:

1. **ERP Servientrega** → ErpSimulatorService con datos mock
2. **SMS** → Logs en consola simulando envío
3. **Email** → Logs en consola simulando envío
4. **Validación de guías** → Respuestas predefinidas

---

## Estructura del Proyecto

```
casilleroback/
├── src/
│   ├── main/
│   │   ├── java/com/servientrega/locker/
│   │   │   ├── controller/
│   │   │   ├── service/
│   │   │   │   ├── notification/
│   │   │   │   └── monitoring/
│   │   │   ├── repository/
│   │   │   ├── entity/
│   │   │   ├── dto/
│   │   │   ├── enums/
│   │   │   ├── security/
│   │   │   ├── config/
│   │   │   ├── exception/
│   │   │   └── LockerApplication.java
│   │   └── resources/
│   │       ├── db/migration/
│   │       ├── application.yml
│   │       └── application-prod.yml
│   └── test/
├── Dockerfile
├── docker-compose.yml
├── pom.xml
├── README.md
├── API_DOCUMENTATION.md
└── PLAN_IMPLEMENTACION.md
```

---

## Comandos Útiles

### Desarrollo
```bash
# Iniciar servicios
docker-compose up -d

# Ver logs
docker-compose logs -f backend

# Detener servicios
docker-compose down

# Rebuild
docker-compose up -d --build
```

### Testing
```bash
# Tests unitarios
mvn test

# Tests de integración
mvn verify

# Cobertura
mvn jacoco:report
```

---

## Próximos Pasos

1. Revisar y aprobar este plan
2. Comenzar con Fase 0
3. Revisiones al final de cada fase
4. Ajustar tiempos según avance real

---

**Fecha de Creación:** 2024  
**Versión:** 1.0  
**Autor:** Amazon Q Developer
