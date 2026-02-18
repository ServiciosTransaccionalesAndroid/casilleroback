# Backend Casilleros Servientrega

Sistema de gestión de lockers inteligentes para Servientrega desarrollado con Spring Boot, PostgreSQL y Docker.

## Requisitos Previos

- Docker y Docker Compose
- Java 17+ (solo para desarrollo local sin Docker)
- Maven 3.8+ (solo para desarrollo local sin Docker)

## Inicio Rápido con Docker

### 1. Clonar el repositorio
```bash
cd /home/josesilva/casilleroback
```

### 2. Iniciar servicios
```bash
docker-compose up -d
```

Este comando iniciará:
- **PostgreSQL** en puerto 5432
- **Backend Spring Boot** en puerto 8080
- **Adminer** (administrador de BD) en puerto 8081

### 3. Verificar que funciona
```bash
curl http://localhost:8080/api/health
```

### 4. Acceder a Swagger UI
```
http://localhost:8080/swagger-ui.html
```

### 5. Acceder a Adminer (Administrador de BD)
```
http://localhost:8081
```
**Credenciales:**
- Sistema: PostgreSQL
- Servidor: postgres
- Usuario: locker_user
- Contraseña: locker_pass
- Base de datos: locker_db

## Comandos Útiles

### Ver logs del backend
```bash
docker-compose logs -f backend
```

### Ver logs de PostgreSQL
```bash
docker-compose logs -f postgres
```

### Detener servicios
```bash
docker-compose down
```

### Detener y eliminar volúmenes (limpia la BD)
```bash
docker-compose down -v
```

### Rebuild de la aplicación
```bash
docker-compose up -d --build
```

### Reiniciar solo el backend
```bash
docker-compose restart backend
```

## Desarrollo Local (sin Docker)

### 1. Iniciar PostgreSQL con Docker
```bash
docker run -d \
  --name locker-postgres \
  -e POSTGRES_DB=locker_db \
  -e POSTGRES_USER=locker_user \
  -e POSTGRES_PASSWORD=locker_pass \
  -p 5432:5432 \
  postgres:15-alpine
```

### 2. Ejecutar la aplicación
```bash
mvn spring-boot:run
```

### 3. Ejecutar tests
```bash
mvn test
```

### 4. Compilar
```bash
mvn clean package
```

## Estructura del Proyecto

```
casilleroback/
├── src/
│   ├── main/
│   │   ├── java/com/servientrega/locker/
│   │   │   ├── controller/       # Controladores REST
│   │   │   ├── service/          # Lógica de negocio
│   │   │   ├── repository/       # Repositorios JPA
│   │   │   ├── entity/           # Entidades JPA
│   │   │   ├── dto/              # DTOs
│   │   │   ├── enums/            # Enumeraciones
│   │   │   ├── security/         # Seguridad y JWT
│   │   │   ├── config/           # Configuraciones
│   │   │   ├── exception/        # Manejo de excepciones
│   │   │   └── LockerApplication.java
│   │   └── resources/
│   │       ├── db/migration/     # Scripts Flyway
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       └── application-prod.yml
│   └── test/
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── README.md
```

## Endpoints Principales

### Health Check
- `GET /api/health` - Verificar estado del servicio

### Paquetes
- `GET /api/packages/validate?trackingNumber={number}` - Validar paquete

### Depósitos
- `POST /api/deposits` - Registrar depósito

### Retiros
- `GET /api/retrievals/validate?code={code}` - Validar código de retiro
- `POST /api/retrievals` - Registrar retiro

### Lockers
- `POST /api/lockers/status-update` - Actualizar estado de casillero
- `GET /api/lockers/{lockerId}/status` - Consultar estado
- `GET /api/lockers/{lockerId}/compartments` - Listar casilleros

### Autenticación
- `POST /api/auth/courier/login` - Login de mensajero

## Variables de Entorno

| Variable | Descripción | Valor por defecto |
|----------|-------------|-------------------|
| SPRING_PROFILES_ACTIVE | Perfil activo | dev |
| SPRING_DATASOURCE_URL | URL de PostgreSQL | jdbc:postgresql://postgres:5432/locker_db |
| SPRING_DATASOURCE_USERNAME | Usuario de BD | locker_user |
| SPRING_DATASOURCE_PASSWORD | Contraseña de BD | locker_pass |

## Tecnologías

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **Spring Security**
- **PostgreSQL 15**
- **Flyway** (migraciones)
- **JWT** (autenticación)
- **Swagger/OpenAPI** (documentación)
- **Docker & Docker Compose**
- **Lombok**
- **Maven**

## Estado del Proyecto

✅ **Fase 0 Completada:** Configuración inicial
- Estructura del proyecto
- Docker y PostgreSQL configurados
- Dependencias Maven
- Configuración básica de Spring Boot
- Health check endpoint
- Swagger UI

🚧 **Próximas Fases:**
- Fase 1: Modelo de datos y entidades
- Fase 2: Servicios core y simulación ERP
- Fase 3: API REST completa
- Fase 4: Sistema de notificaciones
- Fase 5: Gestión de alertas
- Fase 6: Seguridad JWT completa

## Soporte

Para más información, consultar el archivo `PLAN_IMPLEMENTACION.md`
